package pixelmon.entities.pixelmon.helpers;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import pixelmon.PixelmonEntityList;
import pixelmon.battles.BattleController;
import pixelmon.battles.Moveset;
import pixelmon.battles.attacks.Attack;
import pixelmon.battles.attacks.statusEffects.StatusEffectBase;
import pixelmon.battles.participants.IBattleParticipant;
import pixelmon.battles.participants.PlayerParticipant;
import pixelmon.battles.participants.TrainerParticipant;
import pixelmon.battles.participants.WildPixelmonParticipant;
import pixelmon.comm.ChatHandler;
import pixelmon.database.BattleStats;
import pixelmon.database.DatabaseMoves;
import pixelmon.database.DatabaseStats;
import pixelmon.database.EvolutionInfo;
import pixelmon.database.Stats;
import pixelmon.entities.EntityTrainer;
import pixelmon.entities.pixelmon.BaseEntityPixelmon;
import pixelmon.entities.pixelmon.EntityWaterPixelmon;
import pixelmon.entities.pixelmon.helpers.PixelmonEntityHelper;
import pixelmon.enums.EnumGui;
import pixelmon.enums.EnumPokeballs;
import pixelmon.enums.EnumType;
import pixelmon.gui.GuiScreenPokeChecker;
import pixelmon.gui.pokedex.GuiPokedex;
import pixelmon.items.ItemEvolutionStone;

import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityAITasks;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityLookHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Vec3D;
import net.minecraft.src.World;
import net.minecraft.src.mod_Pixelmon;
import net.minecraft.src.forge.MinecraftForge;

public class PixelmonEntityHelper {
	private IHaveHelper pixelmon;
	public EnumPokeballs caughtBall;
	public Moveset moveset = new Moveset();
	public Stats stats = new Stats();
	public BattleStats battleStats = new BattleStats();
	public ArrayList<StatusEffectBase> status = new ArrayList<StatusEffectBase>();
	public boolean isMale;
	public String nickname = "";
	public boolean isInBall = true;
	public boolean isFainted = false;
	public boolean doesHover = false;
	public float hoverHeight;
	public int aggression;
	public float scale = 1F;
	public float maxScale = 1.25F;
	public BattleController bc;
	public LevelHelper lvl;

	public PixelmonEntityHelper(IHaveHelper pixelmon) {
		this.pixelmon = pixelmon;
	}

	public void loadMoveset() {
		moveset = DatabaseMoves.GetInitialMoves(getName(), getLvl().getLevel());
	}
	
	public EntityLookHelper getLookHelper() {
		if (pixelmon instanceof BaseEntityPixelmon)
			return ((BaseEntityPixelmon) pixelmon).getLookHelper();
		else if (pixelmon instanceof EntityWaterPixelmon)
			return ((EntityWaterPixelmon) pixelmon).getLookHelper();
		return null;
	}

	public String getName() {
		if (pixelmon instanceof BaseEntityPixelmon) {
			BaseEntityPixelmon p = (BaseEntityPixelmon) pixelmon;
			return p.name;
		} else if (pixelmon instanceof EntityWaterPixelmon) {
			EntityWaterPixelmon p = (EntityWaterPixelmon) pixelmon;
			return p.name;
		}
		return null;
	}

	public String getDisplayName() {
		if (nickname==null || nickname.equals("")) return getName();
		else return nickname;
	}

	public boolean attackEntityFrom(DamageSource dmgSource, int power) {
		boolean flag = ((EntityLiving) pixelmon).attackEntityFrom(dmgSource, power);
		Entity entity = dmgSource.getEntity();
		if (pixelmon instanceof IHaveHelper) {
			if (((IHaveHelper) pixelmon).getHelper().isValidTarget(entity)) {
				((IHaveHelper) pixelmon).getHelper().setAttackTarget((EntityLiving) entity);
				((IHaveHelper) pixelmon).getHelper().setTarget(entity);
			}
		}
		return flag;
	}

	private void setTarget(Entity entity) {
		if (entity instanceof BaseEntityPixelmon)
			((BaseEntityPixelmon) entity).setTarget(entity);
		else if (entity instanceof EntityWaterPixelmon)
			((EntityWaterPixelmon) entity).setTarget(entity);
	}

	private void setAttackTarget(EntityLiving entity) {
		if (entity instanceof BaseEntityPixelmon)
			((BaseEntityPixelmon) entity).setAttackTarget(entity);
		else if (entity instanceof EntityWaterPixelmon)
			((EntityWaterPixelmon) entity).setAttackTarget(entity);
	}

	public ArrayList<EnumType> getType() {
		if (pixelmon instanceof BaseEntityPixelmon)
			return ((BaseEntityPixelmon) pixelmon).type;
		else if (pixelmon instanceof EntityWaterPixelmon)
			return ((EntityWaterPixelmon) pixelmon).type;
		return null;
	}

	public LevelHelper getLvl() {
		if (ModLoader.getMinecraftInstance().theWorld.isRemote) {
			return LevelHelper.readFromLvlString(pixelmon.getLvlString());
		} else {
			if (lvl == null && pixelmon.getLvlString().isEmpty()) {
				lvl = new LevelHelper(this);
				lvl.setLevel(mod_Pixelmon.getRandomNumberBetween(stats.BaseStats.SpawnLevel, stats.BaseStats.SpawnLevel + stats.BaseStats.SpawnLevelRange));
				setHealth(stats.HP);
			}
		}
		return lvl;
	}

	public void EndBattle() {
		pixelmon.EndBattle();
	}

	public int getHealth() {
		if (pixelmon instanceof BaseEntityPixelmon)
			if (((BaseEntityPixelmon) pixelmon).worldObj.isRemote)
				return getLvl().health;
			else
				return ((BaseEntityPixelmon) pixelmon).getHealth();

		else if (pixelmon instanceof EntityWaterPixelmon)
			if (((EntityWaterPixelmon) pixelmon).worldObj.isRemote)
				return getLvl().health;
			else
				return ((EntityWaterPixelmon) pixelmon).getHealth();
		return 0;
	}

	public int getMaxHealth() {
		if (stats == null)
			return 1;
		if (pixelmon.getWorldObj().isRemote)
			return getLvl().maxHealth;
		else
			return stats.HP;
	}

	public void setHealth(int i) {
		if (pixelmon instanceof BaseEntityPixelmon)
			((BaseEntityPixelmon) pixelmon).setHealth(i);
		else if (pixelmon instanceof EntityWaterPixelmon)
			((EntityWaterPixelmon) pixelmon).setHealth(i);
	}


	public void catchInPokeball() {
		pixelmon.catchInPokeball();
	}

	public int getPokemonId() {
		return pixelmon.getPokemonId();
	}

	public void writeEntityToNBT(NBTTagCompound nbt) {
		pixelmon.writeEntityToStorageNBT(nbt);
	}

	public void setLocationAndAngles(double posX, double posY, double posZ, float rotationYaw, float f) {
		pixelmon.setLocationAndAngles(posX, posY, posZ, rotationYaw, f);
	}

	public void setMotion(int i, int j, int k) {
		if (pixelmon instanceof BaseEntityPixelmon)
			((BaseEntityPixelmon) pixelmon).setMotion(i, j, k);
		else if (pixelmon instanceof EntityWaterPixelmon)
			((EntityWaterPixelmon) pixelmon).setMotion(i, j, k);
	}

	public void releaseFromPokeball() {
		pixelmon.releaseFromPokeball();
	}

	public void clearAttackTarget() {
		if (pixelmon instanceof BaseEntityPixelmon) {
			((BaseEntityPixelmon) pixelmon).setTarget(null);
			((BaseEntityPixelmon) pixelmon).setAttackTarget(null);
		} else if (pixelmon instanceof EntityWaterPixelmon) {
			((EntityWaterPixelmon) pixelmon).setTarget(null);
			((EntityWaterPixelmon) pixelmon).setAttackTarget(null);
		}
	}

	public void StartBattle(EntityTrainer entityHit, EntityPlayer opponent) {
		pixelmon.StartBattle(entityHit, opponent);
	}

	public void StartBattle(PixelmonEntityHelper entityHit) {
		pixelmon.StartBattle(entityHit);

	}

	public void setTamed(boolean b) {
		if (pixelmon instanceof BaseEntityPixelmon)
			((BaseEntityPixelmon) pixelmon).setTamed(b);
		else if (pixelmon instanceof EntityWaterPixelmon)
			((EntityWaterPixelmon) pixelmon).setTamed(b);
	}

	public void setLocationAndAngles(IHaveHelper currentPixelmon) {
		if (pixelmon instanceof BaseEntityPixelmon)
			((BaseEntityPixelmon) pixelmon).setLocationAndAngles(currentPixelmon);
		else if (pixelmon instanceof EntityWaterPixelmon)
			((EntityWaterPixelmon) pixelmon).setLocationAndAngles(currentPixelmon);
	}

	public void setPokemonID(int uniqueEntityId) {
		pixelmon.setPokemonId(uniqueEntityId);
	}

	public void setPositionAndRotation(double posX, double posY, double posZ, float rotationYaw, float rotationPitch) {
		pixelmon.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
	}

	public Entity getEntity() {
		return (Entity) pixelmon;
	}

	protected void copyTo(PixelmonEntityHelper entity) {
		entity.setTamed(true);
		entity.nickname = nickname;
		entity.caughtBall = caughtBall;
		entity.moveset.clear();
		entity.isMale = isMale;
		entity.setIsShiny(getIsShiny());
		for (int i = 0; i < moveset.size(); i++)
			entity.moveset.add(moveset.get(i));
		entity.stats.IVs.CopyIVs(stats.IVs);
		entity.getLvl().setLevel(getLvl().getLevel());
		entity.getLvl().setEXP(getLvl().getExp());
		EntityLiving p = (EntityLiving) pixelmon;
		entity.setPositionAndRotation(p.posX, p.posY, p.posZ, p.rotationYaw, p.rotationPitch);
	}

	public void evolve(PixelmonEntityHelper entity) {
		entity.setOwner(this.getOwner());
		if (entity.getOwner() != null)
			ChatHandler.sendChat(entity.getOwner(), "Your " + getName() + " evolved into " + entity.getName() + "!");
		int oldHP = stats.HP;
		copyTo(entity);
		if (nickname.equals(getName()))
			entity.nickname = entity.getName();
		float percentIncrease = ((float) stats.HP) / ((float) oldHP);
		setHealth((int) (((float) getHealth()) * percentIncrease));
		mod_Pixelmon.pokeballManager.getPlayerStorage(entity.getOwner()).replace(this, entity);
		entity.getOwner().worldObj.spawnEntityInWorld((EntityLiving) entity.getEntity());
		entity.setPokemonID(getPokemonId());
		getEntity().setDead();
		entity.getLvl().recalculateXP();
	}

	public IHaveHelper getIHaveHelper() {
		return pixelmon;
	}

	public boolean interact(EntityPlayer entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer entity1 = (EntityPlayer) entity;
			ItemStack itemstack = entity1.getCurrentEquippedItem();
			if (itemstack == null) {
				if (stats.BaseStats.IsRideable && mod_Pixelmon.pokeballManager.getPlayerStorage(entity).isIn(this)) {
					entity.mountEntity((EntityLiving) pixelmon);
					if (pixelmon instanceof BaseEntityPixelmon)
						((BaseEntityPixelmon) pixelmon).resetAI();
					return true;
				}
				return false;
			}

			boolean flag = false;
			if (itemstack.itemID == mod_Pixelmon.rareCandy.shiftedIndex && getOwner() == entity) {
				getLvl().awardEXP(getLvl().getExpToNextLevel() - getLvl().getExp());
				if (!entity.capabilities.isCreativeMode)
					itemstack.stackSize--;
				flag = true;
			}
			if (itemstack.itemID == mod_Pixelmon.pokeChecker.shiftedIndex && getOwner() != null) {
				getOwner().openGui(mod_Pixelmon.instance, EnumGui.PokeChecker.getIndex(), getOwner().worldObj, getPokemonId(), 0, 0); // Pokechecker
				flag = true;
			}
			if (itemstack.itemID == mod_Pixelmon.potion.shiftedIndex && getOwner() == entity) {
				setHealth(getHealth() + 20);
				if (!entity.capabilities.isCreativeMode)
					itemstack.stackSize--;
				if (getHealth() > stats.HP)
					setHealth(stats.HP);
				flag = true;
			}
			// if (itemstack.itemID == mod_Pixelmon.pokeDex.shiftedIndex) {
			// if (getOwner() == entity) {
			// getOwner().openGui(mod_Pixelmon.instance,
			// EnumGui.Pokedex.getIndex(), getOwner().worldObj, getPokemonId(),
			// 0, 0); // Pokedex
			// flag = true;
			// }
			// }
			if (itemstack.getItem() instanceof ItemEvolutionStone && getOwner() == entity) {
				ItemEvolutionStone i = (ItemEvolutionStone) itemstack.getItem();
				for (EvolutionInfo e : getEvolveList()) {
					if (e.evolutionStone == i.getType()) {
						BaseEntityPixelmon evolveTo = (BaseEntityPixelmon) PixelmonEntityList.createEntityByName(e.pokemonName, getOwner().worldObj);
						if (evolveTo == null) {
							System.out.println(e.pokemonName + " isn't coded yet");
							break;
						}
						evolve(evolveTo.helper);
						flag = true;
						itemstack.stackSize--;
						break;
					}
				}
			}
			if (itemstack.stackSize <= 0)
				entity.inventory.setInventorySlotContents(entity.inventory.currentItem, null);
			return flag;
		}

		return false;
	}

	public boolean isValidTarget(Entity entity) {
		if (!(entity instanceof IHaveHelper))
			return false;

		if (getOwner() == ((IHaveHelper) entity).getHelper().getOwner()) {
			return false;
		}

		return true;
	}

	public void readFromNBT(NBTTagCompound var1) {
		getLvl().readFromNBT(var1);
		setHealth(var1.getShort("Health"));
		setPokemonID(var1.getInteger("pixelmonID"));
		stats.readFromNBT(var1);
		if (getHealth() > stats.HP)
			setHealth(stats.HP);
		isInBall = var1.getBoolean("IsInBall");
		nickname = var1.getString("Nickname");
		scale = var1.getFloat("Scale");
		isFainted = var1.getBoolean("IsFainted");
		isMale = var1.getBoolean("IsMale");
		setIsShiny(var1.getBoolean("IsShiny"));
		battleStats.readFromNBT(var1);
		moveset.readFromNBT(var1);
	}

	public void writeToNBT(NBTTagCompound var1) {
		getLvl().writeToNBT(var1);
		var1.setInteger("pixelmonID", getPokemonId());
		var1.setString("Name", getName());
		if (nickname != null && !nickname.equalsIgnoreCase("")) {
			var1.setString("Nickname", nickname);
		} else {
			var1.setString("Nickname", getName());
		}
		var1.setBoolean("IsInBall", isInBall);
		var1.setFloat("Scale", scale);
		var1.setBoolean("IsFainted", isFainted || getIsDead());
		var1.setBoolean("IsMale", isMale);
		var1.setBoolean("IsShiny", getIsShiny());
		battleStats.writeToNBT(var1);
		moveset.writeToNBT(var1);
		stats.writeToNBT(var1);
	}

	public boolean getIsDead() {
		if (pixelmon instanceof BaseEntityPixelmon)
			return ((BaseEntityPixelmon) pixelmon).isDead;
		else if (pixelmon instanceof EntityWaterPixelmon)
			return ((EntityWaterPixelmon) pixelmon).isDead;
		return false;
	}

	public ArrayList<EvolutionInfo> getEvolveList() {
		return DatabaseStats.getEvolveList(getName());
	}

	public void unloadEntity() {
		if (pixelmon instanceof BaseEntityPixelmon)
			((BaseEntityPixelmon) pixelmon).unloadEntity();
		else if (pixelmon instanceof EntityWaterPixelmon)
			((EntityWaterPixelmon) pixelmon).unloadEntity();
	}

	public void setTrainer(EntityTrainer trainer) {
		if (pixelmon instanceof BaseEntityPixelmon) {
			((BaseEntityPixelmon) pixelmon).trainer = trainer;
		} else if (pixelmon instanceof EntityWaterPixelmon) {
			((EntityWaterPixelmon) pixelmon).trainer = trainer;
		}
	}

	public EntityTrainer getTrainer() {
		if (pixelmon instanceof BaseEntityPixelmon) {
			return ((BaseEntityPixelmon) pixelmon).trainer;
		} else if (pixelmon instanceof EntityWaterPixelmon) {
			return ((EntityWaterPixelmon) pixelmon).trainer;
		}
		return null;
	}

	public int getXPos() {
		if (pixelmon instanceof BaseEntityPixelmon) {
			return (int) ((BaseEntityPixelmon) pixelmon).posX;
		} else if (pixelmon instanceof EntityWaterPixelmon) {
			return (int) ((EntityWaterPixelmon) pixelmon).posX;
		}
		return 0;
	}

	public int getYPos() {
		if (pixelmon instanceof BaseEntityPixelmon) {
			return (int) ((BaseEntityPixelmon) pixelmon).posY;
		} else if (pixelmon instanceof EntityWaterPixelmon) {
			return (int) ((EntityWaterPixelmon) pixelmon).posY;
		}
		return 0;
	}

	public int getZPos() {
		if (pixelmon instanceof BaseEntityPixelmon) {
			return (int) ((BaseEntityPixelmon) pixelmon).posZ;
		} else if (pixelmon instanceof EntityWaterPixelmon) {
			return (int) ((EntityWaterPixelmon) pixelmon).posZ;
		}
		return 0;
	}

	public EntityPlayer getOwner() {
		return pixelmon.getOwner();
	}

	public void setOwner(EntityPlayer player) {
		pixelmon.setOwner(player);
	}

	public Vec3D getPosition() {
		if (pixelmon instanceof BaseEntityPixelmon) {
			BaseEntityPixelmon p = (BaseEntityPixelmon) pixelmon;
			return Vec3D.createVector(p.posX, p.posY, p.posZ);
		} else if (pixelmon instanceof EntityWaterPixelmon) {
			EntityWaterPixelmon p = (EntityWaterPixelmon) pixelmon;
			return Vec3D.createVector(p.posX, p.posY, p.posZ);
		}
		return null;
	}

	public void setPosition(Vec3D pos) {
		if (pixelmon instanceof BaseEntityPixelmon) {
			BaseEntityPixelmon p = (BaseEntityPixelmon) pixelmon;
			p.posX = pos.xCoord;
			p.posY = pos.yCoord;
			p.posZ = pos.zCoord;
		} else if (pixelmon instanceof EntityWaterPixelmon) {
			EntityWaterPixelmon p = (EntityWaterPixelmon) pixelmon;
			p.posX = pos.xCoord;
			p.posY = pos.yCoord;
			p.posZ = pos.zCoord;
		}
	}

	private void setIsShiny(boolean isShiny) {
		if (pixelmon instanceof BaseEntityPixelmon)
			((BaseEntityPixelmon) pixelmon).setIsShiny(isShiny);
		else if (pixelmon instanceof EntityWaterPixelmon)
			((EntityWaterPixelmon) pixelmon).setIsShiny(isShiny);
	}

	private boolean getIsShiny() {
		if (pixelmon instanceof BaseEntityPixelmon)
			return ((BaseEntityPixelmon) pixelmon).getIsShiny();
		else if (pixelmon instanceof EntityWaterPixelmon)
			return ((EntityWaterPixelmon) pixelmon).getIsShiny();
		return false;
	}

	public void setIsDead(boolean b) {
		if (pixelmon instanceof BaseEntityPixelmon) {
			((BaseEntityPixelmon) pixelmon).isDead = b;
			if (!b)
				((BaseEntityPixelmon) pixelmon).deathTime = 0;
		}
		if (pixelmon instanceof EntityWaterPixelmon) {
			((EntityWaterPixelmon) pixelmon).isDead = b;
			if (!b)
				((EntityWaterPixelmon) pixelmon).deathTime = 0;
		}
	}

	public void clearVelocity() {
		if (pixelmon instanceof BaseEntityPixelmon) {
			((BaseEntityPixelmon) pixelmon).fallDistance = 0;
			((BaseEntityPixelmon) pixelmon).setVelocity(0, 0, 0);
		}
		if (pixelmon instanceof EntityWaterPixelmon) {
			((EntityWaterPixelmon) pixelmon).fallDistance = 0;
			((EntityWaterPixelmon) pixelmon).setVelocity(0, 0, 0);
		}
	}
}
