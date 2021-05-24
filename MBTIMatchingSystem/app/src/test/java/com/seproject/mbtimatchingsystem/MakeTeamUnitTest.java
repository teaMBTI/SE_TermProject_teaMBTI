package com.seproject.mbtimatchingsystem;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;


public class MakeTeamUnitTest extends TestCase {

    private MakeTeam makeTeam;

    @Before
    public void setUp() {
        makeTeam = new MakeTeam();
    }

    @Test
    public void createTeamTesting() {

        String res = makeTeam.createTeamTest();
        assertEquals("Success", res);
    }

}