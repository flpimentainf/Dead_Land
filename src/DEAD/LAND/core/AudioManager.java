package DEAD.LAND.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class AudioManager {
    public enum Canal {
        MASTER,
        MUSIC,
        SFX,
        AMBIENT
    }

    private static final AudioManager INSTANCIA = new AudioManager();
    private static final String PASTA_AUDIO = "repos/audio/";

    private final Map<Canal, Float> volumes = new HashMap<Canal, Float>();
    private Clip musicaAtual;
    private String musicaAtualId = "";

    private AudioManager() {
        volumes.put(Canal.MASTER, 1.0f);
        volumes.put(Canal.MUSIC, 0.65f);
        volumes.put(Canal.SFX, 0.8f);
        volumes.put(Canal.AMBIENT, 0.55f);
    }

    public static AudioManager getInstancia() {
        return INSTANCIA;
    }

    public void tocarEfeito(String id) {
        tocarClip(id, Canal.SFX, false);
    }

    public void tocarAmbiente(String id) {
        tocarClip(id, Canal.AMBIENT, true);
    }

    public void trocarMusica(String id) {
        if (id == null || id.equals(musicaAtualId)) {
            return;
        }

        pararMusica();
        musicaAtual = criarClip(id);
        musicaAtualId = id;
        if (musicaAtual == null) {
            return;
        }

        aplicarVolume(musicaAtual, Canal.MUSIC);
        musicaAtual.loop(Clip.LOOP_CONTINUOUSLY);
        musicaAtual.start();
    }

    public void pararMusica() {
        if (musicaAtual == null) {
            return;
        }

        musicaAtual.stop();
        musicaAtual.close();
        musicaAtual = null;
        musicaAtualId = "";
    }

    public void definirVolume(Canal canal, float volume) {
        if (canal == null) {
            return;
        }

        volumes.put(canal, Math.max(0f, Math.min(1f, volume)));
        if (canal == Canal.MASTER || canal == Canal.MUSIC) {
            aplicarVolume(musicaAtual, Canal.MUSIC);
        }
    }

    public float getVolume(Canal canal) {
        Float volume = volumes.get(canal);
        return volume == null ? 1.0f : volume;
    }

    private void tocarClip(String id, Canal canal, boolean loop) {
        Clip clip = criarClip(id);
        if (clip == null) {
            return;
        }

        aplicarVolume(clip, canal);
        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        clip.start();
    }

    private Clip criarClip(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        File arquivo = new File(PASTA_AUDIO + id + ".wav");
        if (!arquivo.exists()) {
            return null;
        }

        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(arquivo);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            return clip;
        } catch (Exception e) {
            return null;
        }
    }

    private void aplicarVolume(Clip clip, Canal canal) {
        if (clip == null || !clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }

        float volume = getVolume(Canal.MASTER) * getVolume(canal);
        float volumeLimitado = Math.max(0.0001f, Math.min(1f, volume));
        float ganho = (float) (20.0 * Math.log10(volumeLimitado));
        FloatControl controle = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        controle.setValue(Math.max(controle.getMinimum(), Math.min(controle.getMaximum(), ganho)));
    }
}
