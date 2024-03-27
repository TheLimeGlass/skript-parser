package io.github.syst3ms.skriptparser.variables;

import io.github.syst3ms.skriptparser.file.FileElement;
import io.github.syst3ms.skriptparser.file.FileParser;
import io.github.syst3ms.skriptparser.file.FileSection;
import io.github.syst3ms.skriptparser.log.SkriptLogger;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VariablesTest {

    private static final List<String> LINES = Arrays.asList(
            "databases:",
                "\tram:",
                    "\t\ttest node: true",
                    "\t\tpattern: .*"
            );

    static {
        Variables.registerStorage(RamStorage.class, "ram");
    }

    @Before
    public void setupVariables() {
        Variables.registerStorage(RamStorage.class, "ram");
        SkriptLogger logger = new SkriptLogger(true);
        List<FileElement> elements = FileParser.parseFileLines("database-test", LINES, 0, 1, logger);
        assert elements.size() > 0;
        FileElement element = elements.get(0);
        assert element instanceof FileSection;
        Variables.load(logger, (FileSection) element);
        logger.finalizeLogs();
        logger.close();
        assert Variables.AVAILABLE_STORAGES.size() > 0;
        assert Variables.STORAGES.size() > 0;
        Variables.setVariable("test", "Hello World!", null, false);
    }

    @Test
    public void testVariables() throws InterruptedException {
        Thread.sleep(1);
        assert RamStorage.VARIABLES.containsKey("test") : Arrays.toString(RamStorage.VARIABLES.keySet().toArray(String[]::new));
        Optional<Object> object = Variables.getVariable("test", null, false);
        assert object.isPresent();
        assert object.get().equals("Hello World!");
        Variables.setVariable("test", "Hello New World!", null, false);
        Optional<Object> newObject = Variables.getVariable("test", null, false);
        assert newObject.isPresent();
        assert newObject.get().equals("Hello New World!");
    }

}
