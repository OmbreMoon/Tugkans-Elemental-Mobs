package com.ombremoon.tugkansem.datagen;

import com.ombremoon.tugkansem.CommonClass;
import com.ombremoon.tugkansem.Constants;
import com.ombremoon.tugkansem.common.init.ItemInit;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ItemInit.HANDHELD_LIST.stream().map(Supplier::get).forEach(this::simpleHandHeldModel);
        ItemInit.SIMPLE_ITEM_LIST.stream().map(Supplier::get).forEach(this::simpleGeneratedModel);
        ItemInit.SPAWN_EGG_LIST.stream().map(RegistryObject::get).forEach(this::spawnEggModel);

        tempItem(ItemInit.DEBUG);
    }

    protected ItemModelBuilder simpleGeneratedModel(Item item) {
        return simpleModel(item, mcLoc("item/generated"));
    }

    protected ItemModelBuilder simpleHandHeldModel(Item item) {
        return simpleModel(item, mcLoc("item/handheld"));
    }

    protected ItemModelBuilder spawnEggModel(Item item) {
        return existingParent(item, mcLoc("item/template_spawn_egg"));
    }

    protected ItemModelBuilder simpleModel(Item item, ResourceLocation parent) {
        String name = getName(item);
        return singleTexture(name, parent, "layer0", modLoc("item/" + name));
    }

    protected ItemModelBuilder existingParent(Item item, ResourceLocation parent) {
        String name = getName(item);
        return withExistingParent(name, parent);
    }

    protected ItemModelBuilder tempItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                CommonClass.customLocation("item/" + "temp_texture"));
    }

    protected String getName(Item item) {
        return ForgeRegistries.ITEMS.getKey(item).getPath();
    }

    protected String getName(Block item) {
        return ForgeRegistries.BLOCKS.getKey(item).getPath();
    }

}
