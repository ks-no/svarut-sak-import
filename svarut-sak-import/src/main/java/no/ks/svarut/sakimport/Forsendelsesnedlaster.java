package no.ks.svarut.sakimport;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Forsendelsesnedlaster {

    String urlSti = "/tjenester/svarinn/mottaker/hentNyeForsendelser";

    private SakImportConfig config;

    public Forsendelsesnedlaster(SakImportConfig config) {
        this.config = config;
    }

    public List<Forsendelse> hentNyeForsendelser() {
        return hentForsendelser(config.httpClientForSvarUt());
    }

    private List<Forsendelse> hentForsendelser(HttpClient httpClient) {
        HttpResponse response = null;
        try {
            HttpGet get = new HttpGet(config.svarUtHost() + urlSti);



            response = httpClient.execute(get);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new RuntimeException("Finner ikke tjenestesiden, sjekk at oppgitt url er riktig.");
            }

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED || response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
                System.out.println("Bruker har ikke tilgang til SvarUt eller bruker/passord er feil.");
                System.out.println(response);
                throw new RuntimeException("Bruker har ikke tilgang til SvarUt eller bruker/passord er feil.");
            }

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                System.out.println("SvarUt er ikke tilgjengelig på dette tidspunkt.");
                System.out.println(response);
                System.exit(-1);
            }

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.out.println("Noe gikk galt ved henting av filer.");
                throw new RuntimeException("noe gikk galt");
            }

            String json = EntityUtils.toString(response.getEntity());
            System.out.println("JSon " + json);
            List<Forsendelse> forsendelser = konverterTilObjekt(json);
            return forsendelser;
        } catch (Exception e) {
            throw new RuntimeException("feil under http get", e);
        } finally {
            if (response != null)
                EntityUtils.consumeQuietly(response.getEntity());
        }
    }




    private List<Forsendelse> konverterTilObjekt(String result) {
        JsonDeserializer<DateTime> deser = new JsonDeserializer<DateTime>() {
            @Override
            public DateTime deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context) {
                return json == null ? null : new DateTime(json.getAsLong());
            }
        };

        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, deser).create();

        Type listType = new TypeToken<ArrayList<Forsendelse>>() {
        }.getType();
        return gson.fromJson(result, listType);
    }



    public Fil hentForsendelseFil(Forsendelse forsendelse) {
        final HttpGet httpGet = new HttpGet(forsendelse.getDownloadUrl());
        final HttpResponse response;
        try {
            response = config.httpClientForSvarUt().execute(httpGet);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                throw new RuntimeException("Klarte ikke å laste ned fil for forsendelse " + forsendelse.getId() + ". http status " + response.getStatusLine().getStatusCode());
            }
            final String contentType = response.getEntity().getContentType().getValue();
            final String filename = response.getFirstHeader("Filename").getValue();
            return new Fil(EntityUtils.toByteArray(response.getEntity()), contentType, filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void kvitterForsendelse(Forsendelse forsendelse) {
        throw new IllegalArgumentException();
    }
}
