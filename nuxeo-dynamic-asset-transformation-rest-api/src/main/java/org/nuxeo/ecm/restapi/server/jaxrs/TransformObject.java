package org.nuxeo.ecm.restapi.server.jaxrs;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;
import org.nuxeo.labs.asset.transformation.api.Transformation;
import org.nuxeo.labs.asset.transformation.api.TransformationBuilder;
import org.nuxeo.labs.asset.transformation.service.DynamicTransformationService;
import org.nuxeo.runtime.api.Framework;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/transform")
@WebObject(type = "transform")
@Produces({ MediaType.APPLICATION_JSON })
public class TransformObject extends DefaultObject {

    @GET
    @Path("/{id}")
    public Object getTransform(
            @PathParam("id") String documentId,
            @QueryParam("width") int width,
            @QueryParam("height") int height,
            @QueryParam("format") String format,
            @QueryParam("crop") String crop
    ) {

        CoreSession session = getContext().getCoreSession();
        DocumentModel document = session.getDocument(new IdRef(documentId));

        DynamicTransformationService service = Framework.getService(DynamicTransformationService.class);
        Transformation transformation = new TransformationBuilder(document).width(width).height(height).cropBox(crop).format(format).build();

        return service.transform(document, transformation);
    }
}