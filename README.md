# Description
This repository contains a plugin to provide on the fly asset transformation capabilities to the nuxeo platform

# How to build

In addition to the nuxeo maven repositories, the [GitHub maven repository credentials](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-with-a-personal-access-token) must be configured in `~/.m2/settings.xml` 

```xml
    <server>
      <id>github</id>
      <username></username>
      <password></password>
      <configuration>
        <timeout>3000</timeout>
      </configuration>
    </server>
```

Then run:

```
git clone https://github.com/nuxeo-sandbox/nuxeo-dynamic-asset-transformation
cd nuxeo-dynamic-asset-transformation
mvn clean install
```

# Features
## Transformation APIs
### Image Transformation REST API Endpoint
```
GET /nuxeo/site/api/v1/transform/{docId}
```

Query Parameters:

| Name             | Description                                                                                                                                                                                                                          | Type    | Required | Default value |
|:-----------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|:---------|:--------------|
| width            | Width of the output                                                                                                                                                                                                                  | Integer | false    | Source width  |
| height           | Height of the output                                                                                                                                                                                                                 | Integer | false    | Source height |
| crop             | Crop coordinates in pixels within the source image. x,y,width,height where is x is left, y is top, width and height of the cropped area in pixels                                                                                    | String  | false    |               |
| autoCropRatio    | Crop the image using a target dimension aspect like 3/2 (1.5), 16/9 (1.77) ...                                                                                                                                                       | Float   | false    |               |
| backgroundColor  | The background color to use for the output. See [ImageMagick's documentation](https://imagemagick.org/script/command-line-options.php#fill) for possible color formats. Ex: `#ddddff`, `rgb(255,255,255)`, `white` ...               | String  | false    | white         |
| colorSpace       | The output image colorspace. Any colorspace supported by imagemagick on the server                                                                                                                                                   | String  | false    | sRGB          |
| format           | File format of the output: jpg, png or any other format supported by imagemagick on the server                                                                                                                                       | String  | false    | jpg           |
| compressionLevel | See [ImageMagick's documentation](https://imagemagick.org/script/command-line-options.php#quality) to learn how to use this parameter                                                                                                | Integer | false    | 90            |
| textWatermark    | The text to insert in the image                                                                                                                                                                                                      | String  | false    |               |
| watermarkId      | The Id of an image document in Nuxeo to insert in the image                                                                                                                                                                          | String  | false    |               |
| watermarkGravity | See [ImageMagick's documentation](https://imagemagick.org/script/command-line-options.php#gravity) to learn how to use this parameter. Some possible values are: `Center`, `North`, `West`, `SouthWest` ... `Tile` is a custom value | String  | false    | Tile          |

Example:
```
GET /nuxeo/site/api/v1/transform/docId?width=150&height=515&format=jpg&crop=1084,515,42,129
```

### Image Transformation Automation Operation

Operation ID: Blob.ImageTransform

Take a Blob or Document as input. The parameters are the same as the one available for the REST API endpoint.


### Video Transformation Automation Operation

Operation ID: Blob.VideoTransform

Take a Blob or Document as input. The parameters are the same as for the Blob.ImageTransform operation with two additional parameters for the video and audio codecs.

| Name                | Description                               | Type   | Required | Default value |
|:--------------------|:------------------------------------------|--------|:---------|:--------------|
| format              | The container type for the video file     | String | false    | mp4           |
| videoCodec          | The codec to use to encode video streams  | String | false    | x264          |
| audioCodec          | The audio code to to encode audio streams | String | false    | acc           |

The values that can be used depends of what codecs are installed on the server. 


## Serving images to downstream applications
### Public endpoint

```
GET /nuxeo/site/public/transform/{repository}/{docId}
```

This endpoint relies on the authentication scheme provided by [Nuxeo Public Download Link](https://connect.nuxeo.com/nuxeo/site/marketplace/package/nuxeo-public-download-link) 
A download permission must first be set with this [operation](https://github.com/nuxeo-sandbox/nuxeo-public-download-link#create-a-download-link). 
However, the token doesn't need to be passed in requests to the endpoint

Query params are the same as the ones available for the [private endpoint]().

### Cloudfront integration

An edge lambda function is used to validate the link by calling the Nuxeo Application on the [origin request event](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/lambda-cloudfront-trigger-events.html).
By using this particular event, the lambda function is called only if the target file is not already cached in cloudfront.

Setup information available [here](https://github.com/nuxeo-sandbox/nuxeo-dynamic-asset-transformation/blob/master/aws/nuxeo-dynamic-asset-transformation-edge-lambda/README.md)


## Webui

### Crop/Resize Dialog
An image crop dialog is available in the plugin. The dialog enables user to create crops, download or generate CDN links.

The action is available for all documents with the Picture facet by default.

![UI action screenshot](https://github.com/nuxeo-sandbox/nuxeo-dynamic-asset-transformation/blob/master/documentation_assets/screenshot_action.png)

The action opens a dialog from where users can create and revoke public download links

![UI Dialog screenshot](https://github.com/nuxeo-sandbox/nuxeo-dynamic-asset-transformation/blob/master/documentation_assets/screenshot_dialog.png)

### Download as Action

A `Download as` action is available for documents with the Picture or Video facets.

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
