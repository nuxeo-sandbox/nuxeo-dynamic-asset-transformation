'use strict';

const https = require('https');
const url = require('url');

const transformPath = '/transform/';
const nuxeoDomainHeader = 'nx-domain';

exports.handler = (event, context, callback) => {
    const request = event.Records[0].cf.request;
    console.log('Original request: '+JSON.stringify(request));

    const uri = request.uri;

    const nuxeoHostName = request.origin.s3.customHeaders[nuxeoDomainHeader][0].value;
    console.log('Nuxeo Domain: '+nuxeoHostName);

    if (uri.startsWith(transformPath)) {

        const nuxeoUrl = `/nuxeo/site/public`+uri+'?'+request.querystring;
        console.log('Nuxeo URL: '+ nuxeoUrl);

        const options = {
            hostname: nuxeoHostName,
            port: 443,
            path: nuxeoUrl,
            method: 'GET',
        }

        https.get(options, (res) => {
            console.log('Nuxeo Response Code: '+res.statusCode);
            if (res.statusCode !== 302) {
                callback(Error('Not Found'));
            } else {
                const location = url.parse(res.headers.location);
                console.log('Nuxeo Location: '+ JSON.stringify(location));
                request.uri = location.pathname;
                console.log('Modified request: '+JSON.stringify(request));
                callback(null, request);
            }
        }).on('error', (e) => {
            console.log('Nuxeo Error '+e);
            callback(Error(e))
        })
    } else {
        callback(null, request);
    }
};