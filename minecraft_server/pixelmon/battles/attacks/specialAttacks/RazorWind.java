package pixelmon.battles.attacks.specialAttacks;

import java.util.ArrayList;

import pixelmon.battles.attacks.Attack;
import pixelmon.battles.attacks.attackEffects.EffectBase;
import pixelmon.battles.attacks.statusEffects.StatusEffectBase;
import pixelmon.battles.attacks.statusEffects.StatusEffectType;
import pixelmon.comm.ChatHandler;
import pixelmon.entities.pixelmon.helpers.PixelmonEntityHelper;
import pixelmon.enums.EnumType;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.ModLoader;

public class RazorWind extends MultiTurnSpecialAttackBase {

	public RazorWind() {
		super(MultiTurnSpecialAttackType.RazorWind, 2);
	}

	@Override
	public boolean ApplyEffect(PixelmonEntityHelper user, PixelmonEntityHelper target, Attack a, ArrayList<String> attackList) {
		if (!persists){
			persists = true;
			turnCounter=0;
		}
		turnCounter++;
		if (turnCounter == 1){
			ChatHandler.sendChat(user.getOwner(), target.getOwner(), user.getName() + " whips up a strong wind!");
			return true;
		}else{
			persists = false;
			return false;
		}

	}

	@Override
	public boolean cantMiss() {
		if (turnCounter ==0) return true;
		return false;
	}

}
