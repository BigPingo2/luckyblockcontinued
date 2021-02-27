package mod.lucky.structure;

import mod.lucky.Lucky;
import mod.lucky.drop.DropSingle;
import mod.lucky.drop.func.DropFunc;
import mod.lucky.drop.func.DropProcessData;
import mod.lucky.drop.value.DropStringUtils;
import mod.lucky.drop.value.ValueParser;
import mod.lucky.util.LuckyReader;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.io.InputStreamReader;
import java.util.ArrayList;

public class LuckyStructure extends Structure {
    private ArrayList<DropSingle> blockDrops;
    private ArrayList<DropSingle> entityDrops;

    @Override
    public void process(DropProcessData processData) {
        DropSingle drop = processData.getDropSingle();
        Vector3d harvestPos = drop.getVecPos();
        int rotation = drop.getPropertyInt("rotation");
        BlockPlacer blockPlacer = new BlockPlacer(processData.getRawWorld());

        if (this.blockMode == BlockMode.REPLACE) {
            StructureUtils.fillWithAir(blockPlacer, this.size,
                this.centerPos, harvestPos, rotation);
        }

        DropProcessData blockProcessData = processData.copy();
        blockProcessData.setProcessType(DropProcessData.EnumProcessType.LUCKY_STRUCT);
        for (DropSingle blockDropRaw : this.blockDrops) {
            DropSingle blockDrop = blockDropRaw.initialize(blockProcessData);
            blockProcessData.setDrop(blockDrop);

            BlockState blockStateInit = blockDrop.getBlockState();
            BlockPos blockPos = blockDrop.getBlockPos();

            BlockState blockState = StructureUtils.applyBlockMode(
                this.blockMode, blockStateInit);
            if (blockState != null) {
                StructureUtils.setBlock(blockPlacer, blockState,
                    blockPos, this.centerPos,
                    harvestPos, rotation);

                if (this.blockMode != BlockMode.AIR && blockDrop.hasProperty("tileEntity")) {
                    StructureUtils.setTileEntity(
                        blockProcessData.getRawWorld(),
                        blockDrop.getPropertyNBT("tileEntity"),
                        blockPos, this.centerPos,
                        harvestPos, rotation);
                }
            }
        }

        DropProcessData entityProcessData = processData.copy();
        entityProcessData.setProcessType(DropProcessData.EnumProcessType.LUCKY_STRUCT);
        for (DropSingle entityDrop : this.entityDrops) {
            entityDrop = entityDrop.initialize(entityProcessData);
            entityProcessData.setDrop(entityDrop);

            Vector3d originalPos = entityDrop.getVecPos();
            if (entityDrop.getPropertyNBT("NBTTag") != null) {
                CompoundNBT entityTag = StructureUtils.rotateEntityNBT(
                    entityDrop.getPropertyNBT("NBTTag"),
                    harvestPos.add(this.centerPos),
                    rotation);
                entityDrop.setProperty("NBTTag", entityTag);
            }
            entityDrop.setVecPos(StructureUtils.getWorldPos(
                originalPos, this.centerPos, harvestPos, rotation));
            DropFunc.getDropFunction("entity").process(entityProcessData);
        }

        if (this.blockUpdate) blockPlacer.update();
        this.processOverlay(processData);
    }

    @Override
    public void readFromFile() {
        try {
            LuckyReader reader = new LuckyReader(
                new InputStreamReader(this.openFileStream()));

            String section = "";
            String curLine;

            this.blockDrops = new ArrayList<DropSingle>();
            this.entityDrops = new ArrayList<DropSingle>();
            int sizeX = 0; int sizeY = 0; int sizeZ = 0;

            while ((curLine = reader.readLine()) != null) {
                if (curLine.startsWith(">")) {
                    section = curLine;
                    continue;
                }

                if (section.equals(">properties")) {
                    String name = curLine.substring(0, curLine.indexOf('='));
                    String value = curLine.substring(curLine.indexOf('=') + 1);

                    if (name.equals("length")) sizeX = ValueParser.getInteger(value);
                    if (name.equals("width")) sizeZ = ValueParser.getInteger(value);
                    if (name.equals("height")) sizeY = ValueParser.getInteger(value);
                }
                if (section.equals(">blocks")) {
                    DropSingle drop = new DropSingle();
                    String[] properties = DropStringUtils.splitBracketString(curLine, ',');

                    drop.setRawProperty("type", "block");
                    drop.setRawProperty("posX", properties[0]);
                    drop.setRawProperty("posY", properties[1]);
                    drop.setRawProperty("posZ", properties[2]);
                    drop.setRawProperty("ID", properties[3]);
                    if (properties.length > 4) {
                        String stateProp = properties[4];
                        if (!stateProp.trim().equals("")) {
                            try { Integer.parseInt(stateProp); }
                            catch (NumberFormatException e) {
                                // only set blockstate properties if the value is not an integer
                                drop.setRawProperty("state", stateProp);
                            }
                        }
                    }
                    if (properties.length > 5) drop.setRawProperty("tileEntity", properties[5]);
                    this.blockDrops.add(drop);
                }
                if (section.equals(">entities")) {
                    DropSingle drop = new DropSingle();
                    String[] properties = DropStringUtils.splitBracketString(curLine, ',');

                    drop.setRawProperty("type", "entity");
                    drop.setRawProperty("posX", properties[0]);
                    drop.setRawProperty("posY", properties[1]);
                    drop.setRawProperty("posZ", properties[2]);
                    drop.setRawProperty("ID", properties[3]);
                    if (properties.length > 4) drop.setRawProperty("NBTTag", properties[4]);
                    this.blockDrops.add(drop);
                }
            }
            reader.close();

            this.size = new BlockPos(sizeX, sizeY, sizeZ);
            this.initCenterPos();
        } catch (Exception e) {
            Lucky.error(e, "Error loading structure '" + this.id + "'");
        }
    }
}
