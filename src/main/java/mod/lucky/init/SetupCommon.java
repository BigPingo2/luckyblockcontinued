package mod.lucky.init;

import mod.lucky.Lucky;
import mod.lucky.block.BlockLuckyBlock;
import mod.lucky.crafting.RecipeAddons;
import mod.lucky.crafting.RecipeLuckCrafting;
import mod.lucky.drop.func.DropFunc;
import mod.lucky.entity.EntityLuckyPotion;
import mod.lucky.entity.EntityLuckyProjectile;
import mod.lucky.item.ItemLuckyBlock;
import mod.lucky.item.ItemLuckyBow;
import mod.lucky.item.ItemLuckyPotion;
import mod.lucky.item.ItemLuckySword;
import mod.lucky.network.PacketHandler;
import mod.lucky.resources.loader.PluginLoader; import mod.lucky.resources.loader.ResourceManager;
import mod.lucky.structure.Structure;
import mod.lucky.tileentity.TileEntityLuckyBlock;
import mod.lucky.world.LuckyWorldFeature;
import mod.lucky.world.LuckyTickHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SetupCommon {
    public static EntityType<EntityLuckyPotion> ENTITY_LUCKY_POTION;
    public static EntityType<EntityLuckyProjectile> ENTITY_LUCKY_PROJECTILE;
    public static TileEntityType<TileEntityLuckyBlock> TE_LUCKY_BLOCK;

    public static IRecipeSerializer<RecipeLuckCrafting> LUCK_CRAFTING;
    public static IRecipeSerializer<RecipeAddons> ADDON_CRAFTING;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(Lucky.luckyBlock);
        for (PluginLoader plugin : Lucky.luckyBlockPlugins)
            event.getRegistry().register(plugin.getBlock());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(
            new ItemLuckyBlock(Lucky.luckyBlock)
                .setRegistryName(Lucky.luckyBlock.getRegistryName()));
        event.getRegistry().register(Lucky.luckySword);
        event.getRegistry().register(Lucky.luckyBow);
        event.getRegistry().register(Lucky.luckyPotion);

        for (PluginLoader plugin : Lucky.luckyBlockPlugins) {
            event.getRegistry().register(
                new ItemLuckyBlock(plugin.getBlock())
                    .setRegistryName(plugin.getBlock().getRegistryName()));

            if (plugin.getSword() != null) event.getRegistry().register(plugin.getSword());
            if (plugin.getBow() != null) event.getRegistry().register(plugin.getBow());
            if (plugin.getPotion() != null) event.getRegistry().register(plugin.getPotion());
        }

        Lucky.resourceManager.loadAllResources(true);
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        LUCK_CRAFTING = (IRecipeSerializer<RecipeLuckCrafting>)
            new SpecialRecipeSerializer<>(RecipeLuckCrafting::new)
            .setRegistryName(new ResourceLocation("lucky:crafting_luck"));

        ADDON_CRAFTING = (IRecipeSerializer<RecipeAddons>)
            new SpecialRecipeSerializer<>(RecipeAddons::new)
                .setRegistryName(new ResourceLocation("lucky:crafting_addons"));

        event.getRegistry().register(LUCK_CRAFTING);
        event.getRegistry().register(ADDON_CRAFTING);
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        ENTITY_LUCKY_POTION = (EntityType<EntityLuckyPotion>)
            EntityType.Builder.<EntityLuckyPotion>create(
                EntityLuckyPotion::new, EntityClassification.MISC)
                    .setTrackingRange(100)
                    .setUpdateInterval(20)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("lucky_potion")
                    .setRegistryName(new ResourceLocation("lucky:lucky_potion"));

        ENTITY_LUCKY_PROJECTILE = (EntityType<EntityLuckyProjectile>)
            EntityType.Builder.<EntityLuckyProjectile>create(
                EntityLuckyProjectile::new, EntityClassification.MISC)
                    .setTrackingRange(100)
                    .setUpdateInterval(20)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("lucky_projectile")
                    .setRegistryName(new ResourceLocation("lucky:lucky_projectile"));

        event.getRegistry().register(ENTITY_LUCKY_POTION);
        event.getRegistry().register(ENTITY_LUCKY_PROJECTILE);
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        List<Block> validBlocks = Lucky.luckyBlockPlugins.stream()
            .map(p -> p.getBlock()).collect(Collectors.toList());
        validBlocks.add(Lucky.luckyBlock);

        TE_LUCKY_BLOCK = (TileEntityType<TileEntityLuckyBlock>)
            TileEntityType.Builder.create(TileEntityLuckyBlock::new,
                validBlocks.toArray(new Block[validBlocks.size()]))
                    .build(null)
                    .setRegistryName(new ResourceLocation("lucky:lucky_block"));

        event.getRegistry().register(TE_LUCKY_BLOCK);
    }

    public static void registerBiomeFeatures(BiomeLoadingEvent event) {
        for (LuckyWorldFeature feature : Lucky.worldFeatures) {
            ConfiguredFeature<?, ?> configuredGenerator
                = new ConfiguredFeature<NoFeatureConfig, LuckyWorldFeature>(
                feature, new NoFeatureConfig());

            event.getGeneration().getFeatures(GenerationStage.Decoration.SURFACE_STRUCTURES)
                .add(() -> configuredGenerator);
        }
    }

    public static void setupStatic() {
        Lucky.luckyBlock = (BlockLuckyBlock) new BlockLuckyBlock()
            .setRegistryName("lucky_block");
        Lucky.luckySword = (ItemLuckySword) new ItemLuckySword()
            .setRegistryName("lucky_sword");
        Lucky.luckyBow = (ItemLuckyBow) new ItemLuckyBow()
            .setRegistryName("lucky_bow");
        Lucky.luckyPotion = (ItemLuckyPotion) new ItemLuckyPotion()
            .setRegistryName("lucky_potion");

        Lucky.resourceManager = new ResourceManager(new File("."));
        Lucky.tickHandler = new LuckyTickHandler();

        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, SetupCommon::registerBiomeFeatures);
        MinecraftForge.EVENT_BUS.register(Lucky.tickHandler);

        DropFunc.registerFunctions();

        Lucky.resourceManager.registerPlugins();
    }

    public static Structure getStructure(String id) {
        return Lucky.structures.stream()
            .filter(s -> s.id.equals(id))
            .findFirst().get();
    }

    public static void setup() {
        Lucky.resourceManager.extractDefaultResources();
        Lucky.resourceManager.loadAllResources(false);
        PacketHandler.register();
    }
}
