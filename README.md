# Description

This repository contains a plugin to provide on the fly asset transformation to the nuxeo platform

# How to build

```
git clone https://github.com/nuxeo-sandbox/nuxeo-dynamic-asset-transformation
cd nuxeo-dynamic-asset-transformation
mvn clean install
```

# Features

## Image Transformation Endpoint

```
GET /nuxeo/site/api/v1/transform/{docId}
```

parameters are query params:
- width
- height
- format (jpeg, png or webp)
- crop (x,y,width,height) where is x is left, y is top, width and height of the cropped area in pixels
  
Example:

```
GET /nuxeo/site/api/v1/transform/docId?width=150&height=515&format=jpg&crop=1084,515,42,129
```

## Public endpoint (CDN source)

```
GET /nuxeo/site/public/transform/{repository}/{docId}
```

This endpoint relies on the authentication scheme provided by [Nuxeo Public Download Link](https://connect.nuxeo.com/nuxeo/site/marketplace/package/nuxeo-public-download-link) 
A download permission must first be set with [this operation](https://github.com/nuxeo-sandbox/nuxeo-public-download-link#create-a-download-link). 
However the token doesn't need to be passed in requests to the endpoint

query params are the same as the ones available for the private endpoint.

## Webui

An image crop dialog is available in the plugin. The dialog enables user to create crops, download or generate CDN links.

The action is available for all documents with the Picture facetby default.

![UI action screenshot](https://github.com/nuxeo-sandbox/nuxeo-dynamic-asset-transformation/blob/master/documentation_assets/screenshot_action.png)

The action opens a dialog from where users can create and revoke public download links

![UI Dialog screenshot](https://github.com/nuxeo-sandbox/nuxeo-dynamic-asset-transformation/blob/master/documentation_assets/screenshot_dialog.png)


## Cloudfront integration

An edge lambda function is used to validate the link by calling the Nuxeo Application on the [origin request event](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/lambda-cloudfront-trigger-events.html).
By using this particular event, the lambda function is called only if the target file is not already cached in cloudfront.

Setup information available [here](https://github.com/nuxeo-sandbox/nuxeo-dynamic-asset-transformation/blob/master/aws/nuxeo-dynamic-asset-transformation-edge-lambda/README.md)

# Known limitations
This plugin is a work in progress.

# Support

**These features are not part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.

# Nuxeo Marketplace
This plugin is published as 2 packages on the marketplace:
- [Main Package](https://connect.nuxeo.com/nuxeo/site/marketplace/package/nuxeo-dynamic-asset-transformation)
- [Cloudfront Integration Package](https://connect.nuxeo.com/nuxeo/site/marketplace/package/nuxeo-dynamic-asset-transformation-aws)

# License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

# About Nuxeo

Nuxeo Platform is an open source Content Services platform, written in Java. Data can be stored in both SQL & NoSQL databases.

The development of the Nuxeo Platform is mostly done by Nuxeo employees with an open development model.

The source code, documentation, roadmap, issue tracker, testing, benchmarks are all public.

Typically, Nuxeo users build different types of information management solutions for [document management](https://www.nuxeo.com/solutions/document-management/), [case management](https://www.nuxeo.com/solutions/case-management/), and [digital asset management](https://www.nuxeo.com/solutions/dam-digital-asset-management/), use cases. It uses schema-flexible metadata & content models that allows content to be repurposed to fulfill future use cases.

More information is available at [www.nuxeo.com](https://www.nuxeo.com)
