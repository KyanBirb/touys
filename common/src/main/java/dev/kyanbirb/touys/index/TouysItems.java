package dev.kyanbirb.touys.index;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.data.TouysLang;
import dev.kyanbirb.touys.items.bubble_blower.BubbleBlowerItem;
import dev.kyanbirb.touys.items.camcorder.CamcorderItem;
import dev.kyanbirb.touys.items.camcorder.TapeItem;
import dev.kyanbirb.touys.items.camera.CameraItem;
import dev.kyanbirb.touys.items.camera.PhotoItem;
import dev.kyanbirb.touys.items.clone_gun.CloneGun;
import dev.kyanbirb.touys.items.crowbar.CrowbarItem;
import dev.kyanbirb.touys.items.evil_ass_orb.EvilAssOrb;
import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class TouysItems {
	private static final RegistrationProvider<Item> REGISTER = RegistrationProvider.get(BuiltInRegistries.ITEM, SableTouys.MOD_ID);

	public static final RegistryObject<CloneGun> CLONE_GUN = register("clone_gun", CloneGun::new, properties -> properties
			.stacksTo(1)
			.component(TouysComponents.ITEM_DESCRIPTION, 2));

	public static final RegistryObject<EvilAssOrb> EVIL_ASS_ORB = register("evil_ass_orb", EvilAssOrb::new, properties -> properties
			.stacksTo(1)
			.component(TouysComponents.ITEM_DESCRIPTION, 1));

	public static final RegistryObject<CamcorderItem> CAMCORDER = register("camcorder", CamcorderItem::new, properties -> properties
			.stacksTo(1)
			.component(TouysComponents.ITEM_DESCRIPTION, 2));

	public static final RegistryObject<TapeItem> TAPE = register("tape", TapeItem::new, properties -> properties
			.stacksTo(1)
			.component(TouysComponents.ITEM_DESCRIPTION, 2));

	public static final RegistryObject<BubbleBlowerItem> BUBBLE_BLOWER = register("bubble_blower", BubbleBlowerItem::new, properties -> properties
			.stacksTo(1)
			.component(TouysComponents.ITEM_DESCRIPTION, 2));

	public static final RegistryObject<CameraItem> CAMERA = register("camera", CameraItem::new, properties -> properties
			.stacksTo(1)
			.component(TouysComponents.ITEM_DESCRIPTION, 1));

	public static final RegistryObject<PhotoItem> PHOTO = register("photo", PhotoItem::new, properties -> properties
			.stacksTo(1)
			.component(TouysComponents.ITEM_DESCRIPTION, 1));

	public static final RegistryObject<CrowbarItem> CROWBAR = register("crowbar", CrowbarItem::new, properties -> properties
			.stacksTo(1)
			.component(TouysComponents.ITEM_DESCRIPTION, 2)
			.attributes(CrowbarItem.createAttributes(1.0f)));

	private static <T extends Item> RegistryObject<T> register(String id, Function<Item.Properties, T> factory, UnaryOperator<Item.Properties> properties) {
		TouysLang.addItem(id);
		return REGISTER.register(id, () -> factory.apply(properties.apply(new Item.Properties())));
	}

	public static void init() {
		RegistrationProvider.get(Registries.CREATIVE_MODE_TAB, SableTouys.MOD_ID)
				.register("touys", () -> SableTouys.CREATIVE_TAB);
	}
}
