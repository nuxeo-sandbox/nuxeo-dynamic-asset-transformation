const cdk = require('@aws-cdk/core');
const lambda = require('@aws-cdk/aws-lambda');
const iam = require('@aws-cdk/aws-iam');

class NuxeoDynamicAssetTransformationEdgeLambdaStack extends cdk.Stack {
  /**
   *
   * @param {cdk.Construct} scope
   * @param {string} id
   * @param {cdk.StackProps=} props
   */
  constructor(scope, id, props) {
    super(scope, id, props);

    const bucketArnPath = new cdk.CfnParameter(this, "bucketArnPath", {
      type: "String",
      description: "The bucket ARN with object path for permissions"
    });

    const role = new iam.Role(this, 'NuxeoDynamicAssetDeliveryLambdaRole', {
      assumedBy: new iam.CompositePrincipal(new iam.ServicePrincipal("lambda"), new iam.ServicePrincipal("edgelambda"))
    });

    role.addToPolicy(new iam.PolicyStatement({
      resources: ["arn:aws:logs:*:*:*"],
      actions: [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ]
    }));

    role.addToPolicy(new iam.PolicyStatement({
      actions: ["s3:GetObject"],
      resources: [bucketArnPath.valueAsString]
    }));

    // The code that defines your stack goes here
    const handler = new lambda.Function(this, "EdgeAuthHandler", {
      runtime: lambda.Runtime.NODEJS_10_X,
      code: lambda.Code.fromAsset("lambda-resources"),
      handler: "edge-auth.handler",
      role: role
    });

    handler.currentVersion.addAlias('live');
  }
}

module.exports = { NuxeoDynamicAssetTransformationEdgeLambdaStack }
