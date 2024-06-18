package com.tterrag.registrate.builders.client;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class MenuBuilderClient {
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
    
    private static Map<MenuType<?>, MenuScreens.ScreenConstructor<?, ?>> SCREENS = new HashMap<>();
    
    public static <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void register(NonNullSupplier<MenuBuilder.ScreenFactory<T, S>> screenFactory, MenuType<T> ret) {
        MenuBuilder.ScreenFactory<T, S> factory = screenFactory.get();
        MenuScreens.ScreenConstructor<T, S> screenConstructor = factory::create;
        SCREENS.put(ret, screenConstructor);
    }
    
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        try {
            Field mapField = event.getClass().getDeclaredField("registeredScreens");
            mapField.setAccessible(true);

            Map<MenuType<?>, MenuScreens.ScreenConstructor<?, ?>> mapInstance = (Map<MenuType<?>, MenuScreens.ScreenConstructor<?, ?>>) lookup.unreflectGetter(mapField).invoke(event);

            MethodHandle putHandle = lookup.findVirtual(Map.class, "put",
                    MethodType.methodType(Object.class, Object.class, Object.class));

            SCREENS.forEach((ret, screenConstructor) -> {
                try {
                    putHandle.invoke(mapInstance, ret, screenConstructor);
                } catch (Throwable e) {
                    log.fatal(e);
                }
            });
        } catch (Throwable e) {
            log.fatal(e);
        }
    }
}
