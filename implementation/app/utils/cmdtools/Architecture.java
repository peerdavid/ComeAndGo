package utils.cmdtools;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Create architecture dependency graph
 */
public class Architecture {


    /**
     * Creates and displays dependency graph of come and go
     * @param args - not used right now
     * @throws IOException If file could not be created
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Creating dependency graph.");
        createDependencyGraph();
        createImageFromDotFile();
        showArchitectureImage();
        System.out.println("Successfully created architecture.dot file.");
    }


    private static void createDependencyGraph() throws IOException {

        Injector injector = Guice.createInjector(
                new infrastructure.Module(),
                new business.timetracking.Module(),
                new business.notification.Module(),
                new business.usermanagement.Module(),
                new business.reporting.Module(),
                new business.Module());
        writeDotFile("architecture.dot", injector);
    }

    private static void writeDotFile(String filename, Injector demoInjector) throws IOException {
        PrintWriter out = new PrintWriter(new File(filename), "UTF-8");

        com.google.inject.Injector injector = Guice.createInjector(new GraphvizModule());
        GraphvizGrapher grapher = injector.getInstance(GraphvizGrapher.class);
        grapher.setOut(out);
        grapher.setRankdir("TB");
        grapher.graph(demoInjector);
    }


    private static void createImageFromDotFile() throws IOException {
        Runtime rt = Runtime.getRuntime();
        rt.exec("dot -Tps architecture.dot -o architecture.ps");
    }


    private static void showArchitectureImage() throws IOException {
        Runtime rt = Runtime.getRuntime();
        rt.exec("gnome-open architecture.ps");
    }
}
