## Description

CDK script to deploy the lambda@edge function to validate asset transform delivery urls

## How to build

The CDK stack must be deployed in us-east-1

```
git clone https://github.com/nuxeo-sandbox/nuxeo-dynamic-asset-transformation
cd nuxeo-dynamic-asset-transformation/aws/nuxeo-dynamic-asset-transformation-edge-lambda
npm install
cdk bootstrap
cdk synth
cdk deploy --parameters bucketArnPath="arn:aws:s3:::my_bucket_name/my_bucket_prefix/*"
```

## Post deployment steps

This part is not automated and requires manual actions.
Here we assume that a cloudfront distributions already exists and is configured on the nuxeo side.
If not, please have a look at cloudfront integration [documentation](https://doc.nuxeo.com/nxdoc/amazon-cloudfront-distribution/)

### Origin Custom Header

In Cloudfront > Distributions > Your Distribution > Origins and Origin Groups, edit your S3 bucket origin and add an **Origin Custom Headers**

```
nx-domain = my_nuxeo_host
```

### Behavior

In Cloudfront > Distributions > Your Distribution > Behaviors, create a new behavior with the following parameter

```
Path Pattern = transform/*
Origin or Origin Group = your S3 bucket origin
```

### Lambda

In Lambda, open the function created with the CDK stack:
* Go to Configuration > trigger
* Click on **Add Triger**
* Select Cloudfront
* Click on **Deploy to Lambda@Edge**
* Select your distribution, the cache behavior configured in the previous step and select Origin request for the cloudfront event
* click on deploy
