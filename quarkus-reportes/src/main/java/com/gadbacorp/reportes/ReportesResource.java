package com.gadbacorp.reportes;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/reportes/ventas")
@Produces(MediaType.APPLICATION_JSON)
public class ReportesResource {

    @Inject
    ResumenVentas resumen;

    @GET
    @Path("/resumen")
    public ResumenVentas.Resumen resumen() {
        return resumen.snapshot();
    }
}
