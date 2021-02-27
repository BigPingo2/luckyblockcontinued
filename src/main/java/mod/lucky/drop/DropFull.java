package mod.lucky.drop;

import mod.lucky.Lucky;
import mod.lucky.drop.func.DropProcessData;
import mod.lucky.drop.value.DropStringUtils;
import mod.lucky.drop.value.ValueParser;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.Locale;

public class DropFull extends DropBase {
    private String rawDrop;

    private int luck;
    private float chance;
    // used for natural generation
    private boolean setChance;
    private DropBase drop;

    public DropFull() {
        super();
        this.luck = 0;
        this.chance = 1.0F;
    }

    public String readLuckChance(String string) {
        ArrayList<Integer> points = new ArrayList<Integer>();
        int count = 0;
        boolean usedLuckChance = true;
        while (true) {
            int chancePoint =
                string.indexOf(
                    "@chance=",
                    count > 0 ? (usedLuckChance ? points.get(count - 1) : points.get(count - 1) + 1) : 0);
            int luckPoint =
                string.indexOf(
                    "@luck=",
                    count > 0 ? (usedLuckChance ? points.get(count - 1) : points.get(count - 1) + 1) : 0);
            int propertyType;
            int propertyStringLength;

            if ((chancePoint < luckPoint && chancePoint != -1) || (luckPoint == -1 && chancePoint > -1)) {
                propertyType = 0;
                propertyStringLength = "@chance=".length();
                points.add(count, chancePoint);
            } else if ((luckPoint < chancePoint && luckPoint != -1)
                || (chancePoint == -1 && luckPoint > -1)) {
                propertyType = 1;
                propertyStringLength = "@luck=".length();
                points.add(count, luckPoint);

            } else {
                break;
            }

            usedLuckChance = true;
            char[] invalidChars = {'+', '*', '(', ')', '[', ']', '"', ',', ';', '/'};
            if (DropStringUtils.getEndPoint(
                string, points.get(count) + propertyStringLength, invalidChars)
                != string.toCharArray().length) usedLuckChance = false;
            int curEndPoint =
                DropStringUtils.getEndPoint(string, points.get(count) + propertyStringLength, '@');
            String curValue = string.substring(points.get(count) + propertyStringLength, curEndPoint);
            if (curValue.startsWith("(") && curValue.endsWith(")"))
                curValue = curValue.substring(1, curValue.length() - 1);

            if (usedLuckChance) {
                try {
                    if (propertyType == 0) {
                        this.chance = ValueParser.getFloat(curValue);
                        this.setChance = true;
                    }
                    if (propertyType == 1) this.luck = ValueParser.getInteger(curValue);
                } catch (Exception e) {
                    Lucky.error(e, "Error reading luck/chance for drop: " + string);
                }

                String linePart1 = string.substring(0, points.get(count));
                String linePart2 = string.substring(curEndPoint, string.length());
                string = linePart1 + linePart2;
            }
            count++;
        }
        return string;
    }

    public DropBase getDrop() {
        return this.drop;
    }

    public int getLuck() {
        return this.luck;
    }

    public float getChance() {
        return this.chance;
    }

    public void setDrop(DropBase drop) {
        this.drop = drop;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public boolean wasChanceSet() {
        return this.setChance;
    }

    public DropFull copy() {
        DropFull dropFull = new DropFull();
        dropFull.setLuck(this.luck);
        dropFull.setChance(this.chance);
        dropFull.setDrop(this.drop);
        return dropFull;
    }

    @Override
    public DropFull initialize(DropProcessData processData) {
        return this;
    }

    @Override
    public void readFromString(String string) {
        try {
            this.rawDrop = string;
            string = this.readLuckChance(string);
            this.readDropFromString(string);
        } catch (Exception e) {
            Lucky.error(e, "Error reading drop: " + string);
        }
    }

    private void readDropFromString(String string) {
        if (string.toLowerCase(Locale.ENGLISH).startsWith("group")) {
            DropGroup group = new DropGroup();
            group.readFromString(string);
            this.drop = group;
        } else {
            DropSingle properties = new DropSingle();
            properties.readFromString(string);
            this.drop = properties;
        }
    }

    @Override
    public String writeToString() {
        return null;
    }

    @Override
    public void readFromNBT(CompoundNBT tagCompound) {
    }

    @Override
    public CompoundNBT writeToNBT() {
        return null;
    }

    @Override
    public String toString() {
        return this.rawDrop;
    }
}
