package org.n52.project.enforce.geoquest.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;

//@Provider
@Component
public class ResponseInterceptor implements ReaderInterceptor {

    private ProvenanceService provenanceService;

    private static Logger LOG = LoggerFactory.getLogger(ResponseInterceptor.class);

    public void setProvenanceService(ProvenanceService provenanceService) {
        this.provenanceService = provenanceService;
    }

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        InputStream is = context.getInputStream();
        LOG.info("" + (provenanceService == null));
        try {
            // context.proceed();
        } finally {
            ProvenanceEntry entry = null;

            try {
                entry = provenanceService.storeInitialData(is);
            } catch (NoSuchAlgorithmException | IOException e) {
                // TODO Handle exception
                LOG.error(e.getMessage());
                return context.proceed();
            }
            LOG.info("Intercepted");
            context.setInputStream(new ByteArrayInputStream(entry.getData()));
        }
        return context.proceed();
    }
}