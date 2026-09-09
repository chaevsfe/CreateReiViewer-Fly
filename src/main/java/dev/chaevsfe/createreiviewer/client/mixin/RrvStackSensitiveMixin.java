package dev.chaevsfe.createreiviewer.client.mixin;

import java.util.HashMap;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = {
    "com.zurrtum.create.client.compat.rrv.category.DrainingCategory",
    "com.zurrtum.create.client.compat.rrv.category.SpoutFillingCategory"
})
public class RrvStackSensitiveMixin {
    @Redirect(
        method = "register",
        at = @At(value = "INVOKE", target = "Ljava/util/HashMap;get(Ljava/lang/Object;)Ljava/lang/Object;"),
        require = 3
    )
    private static Object createreiviewer$stackSensitiveOrEmpty(HashMap<Object, Object> stackSensitive, Object key) {
        Object views = stackSensitive.get(key);
        return views == null ? List.of() : views;
    }
}
