package mathax.client.mixin;

import mathax.client.systems.hud.Hud;
import mathax.client.systems.hud.HudElement;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import mathax.client.utils.network.versions.Versions;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CrashReport.class)
public class CrashReportMixin {
    @Inject(method = "addStackTrace", at = @At("TAIL"))
    private void onAddStackTrace(StringBuilder stringBuilder, CallbackInfo info) {
        if (Modules.get() != null) {
            stringBuilder.append("\n\n<---| MatHax |--->\n");
            stringBuilder.append("Version: ").append(Versions.getStylized()).append("\n");

            boolean modulesActive = false;
            for (Category category : Modules.loopCategories()) {
                List<Module> modules = Modules.get().getGroup(category);

                boolean active = false;
                for (Module module : modules) {
                    if (module != null && module.isEnabled()) {
                        active = true;

                        if (!modulesActive) {
                            modulesActive = true;
                            stringBuilder.append("\n--> Active Modules <--\n");
                        }

                        break;
                    }
                }

                if (active) {
                    boolean printCategory = true;
                    stringBuilder.append("\n[").append(category.name).append("]\n");

                    for (Module module : modules) {
                        if (printCategory) {
                            stringBuilder.append("\n");
                            printCategory = false;
                        }

                        if (module != null && module.isEnabled()) {
                            stringBuilder.append(module.name).append("\n");
                        }
                    }
                }
            }

            boolean hudActive = false;
            if (Hud.get().enabled) {
                for (HudElement element : Hud.get()) {
                    if (!element.enabled) {
                        continue;
                    }

                    if (!hudActive) {
                        hudActive = true;
                        stringBuilder.append("\n--> Active Hud Elements <--\n");
                    }

                    stringBuilder.append(element.name).append("\n");
                }
            }

            stringBuilder.append(modulesActive || hudActive ? "\n" : "").append("<---------------->\n\n");
        }
    }
}