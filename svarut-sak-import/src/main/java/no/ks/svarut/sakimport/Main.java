package no.ks.svarut.sakimport;

import no.geointegrasjon.rep.arkiv.kjerne.xml_schema._2012_01.Journalpost;
import no.ks.svarut.sakimport.GI.Saksimporter;

import java.util.List;

public class Main {

    public static void main(String... args) {
        SakImportConfig config = new SakImportConfig(args);
        Forsendelsesnedlaster nedlaster = new Forsendelsesnedlaster(config);
        List<Forsendelse> forsendelser = nedlaster.hentNyeForsendelser();

        Saksimporter importer = new Saksimporter();

        for (Forsendelse forsendelse : forsendelser) {
            System.out.println(forsendelse.getId());

            final Journalpost journalpost = importer.importerJournalPost(forsendelse);
            final Fil fil = nedlaster.hentForsendelseFil(forsendelse);
            importer.importerDokument(journalpost,forsendelse.getTittel(), fil.getFilename(), fil.getMimetype(), fil.getBytes(), true);
            nedlaster.kvitterForsendelse(forsendelse);
        }
    }
}

