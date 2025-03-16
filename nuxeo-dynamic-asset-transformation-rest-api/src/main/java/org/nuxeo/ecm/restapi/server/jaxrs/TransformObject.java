package org.nuxeo.ecm.restapi.server.jaxrs;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.io.download.DownloadService;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;
import org.nuxeo.labs.asset.transformation.api.Transformation;
import org.nuxeo.labs.asset.transformation.impl.builder.ImageTransformationBuilder;
import org.nuxeo.labs.asset.transformation.service.DynamicTransformationService;
import org.nuxeo.runtime.api.Framework;

@Path("/transform")
@WebObject(type = "transform")
public class TransformObject extends DefaultObject {

    public static final String DOWNLOAD_URL = "downloadUrl";

    @GET
    @Path("/{id}")
    public Object getTransform(@HeaderParam("accept") String acceptHeader, @PathParam("id") String documentId,
            @QueryParam("width") long width, @QueryParam("height") long height, @QueryParam("format") String format,
            @QueryParam("crop") String crop, @QueryParam("autoCropRatio") double autoCropRatio,
            @QueryParam("textWatermark") String textWatermark, @QueryParam("watermarkId") String watermarkId,
            @QueryParam("watermarkGravity") String watermarkGravity, @QueryParam("colorSpace") String colorSpace,
            @QueryParam("backgroundColor") String backgroundColor,
            @QueryParam("compressionLevel") int compressionLevel) {

        boolean acceptJson = false;
        if (StringUtils.isNotBlank(acceptHeader)) {
            List<String> types = Arrays.asList(acceptHeader.split(","));
            acceptJson = types.contains(MediaType.APPLICATION_JSON);
        }

        CoreSession session = getContext().getCoreSession();
        DocumentModel document = session.getDocument(new IdRef(documentId));

        DynamicTransformationService service = Framework.getService(DynamicTransformationService.class);
        Transformation transformation = new ImageTransformationBuilder(document).width(width)
                                                                                .height(height)
                                                                                .cropBox(crop)
                                                                                .cropRatio(autoCropRatio)
                                                                                .format(format)
                                                                                .textWatermark(textWatermark)
                                                                                .watermarkId(watermarkId)
                                                                                .watermarkGravity(watermarkGravity)
                                                                                .colorSpace(colorSpace)
                                                                                .backgroundColor(backgroundColor)
                                                                                .compressionLevel(compressionLevel)
                                                                                .build();

        Blob result = service.transform(document, transformation);

        DownloadService downloadService = Framework.getService(DownloadService.class);
        String key = downloadService.storeBlobs(List.of(result));
        String downloadPath = downloadService.getDownloadUrl(key);
        String baseUrl = Framework.getProperty("nuxeo.url");
        String fullUrl = String.format("%s/%s", baseUrl, downloadPath);

        if (acceptJson) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(DOWNLOAD_URL, fullUrl);
            return new StringBlob(jsonObject.toString(), MediaType.APPLICATION_JSON);
        } else {
            return Response.status(302).location(URI.create(fullUrl)).build();
        }
    }
}