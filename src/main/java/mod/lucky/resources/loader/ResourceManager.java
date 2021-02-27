package mod.lucky.resources.loader;

import mod.lucky.resources.*;

import java.io.File;
import java.util.ArrayList;

public class ResourceManager {
    private static File PLUGIN_DIR_OLD = new File("/addons/luckyBlock");
    private static File PLUGIN_DIR = new File("/addons/lucky");

    private File minecraftDir;
    private ArrayList<BaseResource> resourceList;
    private DefaultLoader defaultLoader;
    private ArrayList<PluginLoader> pluginLoaders;

    public ResourceManager(File minecraftDir) {
        this.minecraftDir = minecraftDir;
        this.resourceList = new ArrayList<BaseResource>();
        this.registerResource(new ResourceDrops());
        this.registerResource(new ResourceSwordDrops());
        this.registerResource(new ResourceBowDrops());
        this.registerResource(new ResourcePotionDrops());
        this.registerResource(new ResourceLuckCrafting());
        this.registerResource(new ResourceNaturalGen());
        this.registerResource(new ResourceProperties());
        this.registerResource(new ResourceStructuresDir());
        this.registerResource(new ResourceStructures());
        this.registerResource(new ResourceRecipes());
        this.resetLoaders();
    }

    public void resetLoaders() {
        this.defaultLoader = new DefaultLoader(this.minecraftDir);
        this.pluginLoaders = new ArrayList<PluginLoader>();

        File fullPluginDir = new File(this.minecraftDir.getPath() + PLUGIN_DIR.getPath());
        if (!fullPluginDir.exists()) {
            File oldFullPluginDir = new File(this.minecraftDir.getPath() + PLUGIN_DIR_OLD.getPath());
            if (!oldFullPluginDir.exists()) fullPluginDir.mkdirs();
            else fullPluginDir = oldFullPluginDir;
        }

        File[] pluginFiles = fullPluginDir.listFiles();
        for (File plugin : pluginFiles) {
            if (plugin.isDirectory()
                || plugin.getName().endsWith(".zip")
                || plugin.getName().endsWith(".jar")) {
                this.pluginLoaders.add(new PluginLoader(plugin));
            }
        }
    }

    public void registerPlugins() {
        for (PluginLoader pluginLoader : this.pluginLoaders) pluginLoader.registerPlugin();
    }

    public void extractDefaultResources() {
        this.defaultLoader.extractDefaultResources(); }

    public DefaultLoader getDefaultLoader() {
        return this.defaultLoader;
    }

    public ArrayList<PluginLoader> getPluginLoaders() {
        return this.pluginLoaders;
    }

    public void loadAllResources(boolean postInit) {
        for (BaseResource resource : this.resourceList)
            if (postInit == resource.postInit()) this.defaultLoader.loadResource(resource);

        for (PluginLoader pluginLoader : this.pluginLoaders) {
            for (BaseResource resource : this.resourceList)
                if (postInit == resource.postInit()) pluginLoader.loadResource(resource);
        }
    }

    public void registerResource(BaseResource resource) {
        this.resourceList.add(resource);
    }
}
