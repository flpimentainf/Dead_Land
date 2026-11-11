package DEAD.LAND.core;

import java.io.File;
import java.nio.file.Path;

/** O launcher informa caminhos absolutos; executar pelos fontes mantém os caminhos locais. */
public final class CaminhosJogo {
    private CaminhosJogo() {}

    public static File recurso(String caminho) {
        return Path.of(System.getProperty("deadland.recursos", "."))
                .resolve(caminho).toAbsolutePath().normalize().toFile();
    }

    public static File pastaSave() {
        return Path.of(System.getProperty("deadland.save", "save")).toAbsolutePath().normalize().toFile();
    }
}
