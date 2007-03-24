package edu.northwestern.bioinformatics.bering.runtime.filesystem;

import edu.northwestern.bioinformatics.bering.BeringTestCase;
import edu.northwestern.bioinformatics.bering.runtime.Script;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @author Rhett Sutphin
 */
public class ScriptFactoryTest extends BeringTestCase {
    private ScriptFactory factory;
    private File existingScriptFile;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        existingScriptFile = getClassRelativeFile(getClass(),
            "../../test_db/001_out_the_door/001_add_frogs.groovy");
        factory = new ScriptFactory(existingScriptFile, null);
    }

    public void testCreatedScriptName() throws Exception {
        Script created = factory.create();
        assertEquals("001_add_frogs", created.getElementName());
    }
    
    public void testScriptTextRead() throws Exception {
        String expectedContents = FileUtils.readFileToString(existingScriptFile);
        Script created = factory.create();
        assertEquals(expectedContents, created.getScriptText());
    }
}
