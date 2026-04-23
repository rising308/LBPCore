package ca.risingvocal.lbpcore.util;

import ca.risingvocal.lbpcore.LBPCore;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleSpawn {

    private final LBPCore plugin;
    public ParticleSpawn(LBPCore plugin) {this.plugin = plugin;}

    public void particleTickEvent(Player player, ParticleData data) {

        String name = data.getName();
        if (name == null || name.isEmpty()) return;
        if (player.getGameMode().equals(GameMode.SPECTATOR) && !plugin.getConfig().getBoolean("particles.show-in-spectator")) return;

        try {
            Particle particle = Particle.valueOf(name.toLowerCase());
            var builder = particle.builder()
                    .location(player.getLocation().add(0,data.getYOffset(),0))
                    .offset(0.5,0.5,0.5)
                    .count(data.getCount())
                    .extra(0)
                    .receivers(plugin.getConfig().getInt("particles.view-distance"), plugin.getConfig().getBoolean("particles.view-sphere"));

            if (particle == Particle.DUST_COLOR_TRANSITION) {
                int red = data.getR();
                int green = data.getG();
                int blue = data.getB();
                int red1 = data.getR1();
                int green1 = data.getG1();
                int blue1 = data.getB1();
                builder.colorTransition(Color.fromRGB(red, green, blue), Color.fromRGB(red1, green1, blue1), (float)data.getScale());
            } else if (particle == Particle.DUST) {
                int red = data.getR();
                int green = data.getG();
                int blue = data.getB();
                builder.color(1, red, green, blue);
            }

            builder.spawn();
        } catch (IllegalArgumentException e) {
        }

//        if (data.getEffectIndex() == 1) {
//            Particle.FLAME.builder()
//                    .location(player.getLocation().add(0, data.getYOffset(), 0))
//                    .offset(0.5,0.5,0.5)
//                    .count(data.getCount())
//                    .extra(0)
//                    .receivers(72, true)
//                    .spawn();
//
//        }
//
//        if (data.getEffectIndex() == 2) {
//            Particle.ENCHANT.builder()
//                    .location(player.getLocation().add(0, data.getYOffset(), 0))
//                    .offset(0.5,0.5,0.5)
//                    .count(data.getCount())
//                    .extra(0)
//                    .receivers(72, true)
//                    .spawn();
//        }
//
//        if (data.getEffectIndex() == 3) {
//
//
//            Particle.DUST_COLOR_TRANSITION.builder()
//                    .location(player.getLocation().add(0,data.getYOffset(),0))
//                    .offset(0.5,0.5,0.5)
//                    .count(data.getCount())
//                    .extra(0)
//
//                    .receivers(72, true)
//                    .spawn();
//        }
    }
}
