package no.ks.svarut.sakimport.GI;

import no.ks.svarut.sakimport.Main;
import org.junit.Test;

public class TestReelleDataFraTestSvarutTilTestGI {


    @Test
    public void testSvarutTestMotGEOTest() throws Exception {
        String[] args = new String[]{"-konfigurasjonsfil", "testConfig.properties"};
        Main.main(args);
    }
}