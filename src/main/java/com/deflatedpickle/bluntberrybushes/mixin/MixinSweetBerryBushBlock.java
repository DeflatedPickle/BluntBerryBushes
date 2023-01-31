/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.bluntberrybushes.mixin;

import java.util.function.Predicate;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("UnusedMixin")
@Mixin(SweetBerryBushBlock.class)
public abstract class MixinSweetBerryBushBlock {
  @Redirect(
      method = "onEntityCollision",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
  public boolean onEntityCollision(Entity instance, DamageSource source, float amount) {
    var armour =
        ((DefaultedList<ItemStack>) instance.getArmorItems())
            .stream()
                .filter((itemStack -> itemStack.getItem() instanceof ArmorItem))
                .map((itemStack -> (ArmorItem) itemStack.getItem()))
                .toList();

    Predicate<ArmorItem> predicate =
        (ArmorItem item) ->
            item.getSlotType() == EquipmentSlot.FEET && item.getMaterial() != ArmorMaterials.CHAIN;

    var feet = armour.stream().anyMatch((predicate));
    var legs = armour.stream().anyMatch((predicate));

    if (!(feet && legs)) {
      instance.damage(source, amount);
    }
    return false;
  }
}
