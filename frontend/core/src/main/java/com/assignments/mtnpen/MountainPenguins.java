package com.assignments.mtnpen;

import com.assignments.mtnpen.network.DeviceIdProvider;
import com.assignments.mtnpen.network.NetworkManager;
import com.assignments.mtnpen.view.states.game.GameState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.menu.MenuState;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.assignments.mtnpen.view.assetmanager.GameAssetManager;

import java.util.UUID;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class MountainPenguins extends ApplicationAdapter {
    private final DeviceIdProvider deviceIdProvider;
    private final String apiBaseUrl;
    private GameStateManager gsm;

    public MountainPenguins() {
        this(new DesktopDeviceIdProvider(), NetworkManager.DEFAULT_BASE_URL);
    }

    public MountainPenguins(DeviceIdProvider deviceIdProvider, String apiBaseUrl) {
        this.deviceIdProvider = deviceIdProvider;
        this.apiBaseUrl = apiBaseUrl;
    }

    public static class DesktopDeviceIdProvider implements DeviceIdProvider {
        private static final String CACHED_ID = resolveId();

        @Override
        public String getDeviceId() {
            return CACHED_ID;
        }

        private static String resolveId() {
            String override = System.getProperty("mtnpen.deviceId");
            if (override != null && !override.isEmpty()) {
                return override;
            }
            // Makes it possible to have multiple desktop instances with different IDs, but
            // still have a stable ID across runs for each instance.
            return "desktop-" + UUID.randomUUID();
        }
    }

    @Override
    public void create() {
        GameAssetManager.loadAssets();
        gsm = new GameStateManager(deviceIdProvider.getDeviceId(), apiBaseUrl);
        gsm.set(new MenuState(gsm));
    }

    @Override
    public void render() {
        gsm.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        gsm.resize(width, height);
    }

    @Override
    public void pause() {
        gsm.pause();
    }

    @Override
    public void resume() {
        gsm.resume();
    }

    @Override
    public void dispose() {
        while (true) {
            try {
                gsm.pop();
            } catch (Exception e) {
                break;
            }
        }
    }
}
