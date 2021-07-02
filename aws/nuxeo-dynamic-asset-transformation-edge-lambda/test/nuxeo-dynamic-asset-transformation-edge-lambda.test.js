const { expect, matchTemplate, MatchStyle } = require('@aws-cdk/assert');
const cdk = require('@aws-cdk/core');
const NuxeoDynamicAssetTransformationEdgeLambda = require('../lib/nuxeo-dynamic-asset-transformation-edge-lambda-stack');

test('Empty Stack', () => {
    const app = new cdk.App();
    // WHEN
    const stack = new NuxeoDynamicAssetTransformationEdgeLambda.NuxeoDynamicAssetTransformationEdgeLambdaStack(app, 'MyTestStack');
    // THEN
    expect(stack).to(matchTemplate({
      "Resources": {}
    }, MatchStyle.EXACT))
});
