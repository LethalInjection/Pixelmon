package net.minecraft.src;

public final class WorldSettings
{
    /** The seed for the map. */
    private final long seed;

    /** The type of the map. 0 is survival, 1 is creative. */
    private final int gameType;

    /**
     * Switch for the map features. 'true' for enabled, 'false' for disabled.
     */
    private final boolean mapFeaturesEnabled;

    /** True if hardcore mode is enabled */
    private final boolean hardcoreEnabled;
    private final WorldType terrainType;

    public WorldSettings(long par1, int par3, boolean par4, boolean par5, WorldType par6WorldType)
    {
        this.seed = par1;
        this.gameType = par3;
        this.mapFeaturesEnabled = par4;
        this.hardcoreEnabled = par5;
        this.terrainType = par6WorldType;
    }

    /**
     * Returns the seed for the world.
     */
    public long getSeed()
    {
        return this.seed;
    }

    /**
     * Returns the world game type.
     */
    public int getGameType()
    {
        return this.gameType;
    }

    /**
     * Returns true if hardcore mode is enabled, otherwise false
     */
    public boolean getHardcoreEnabled()
    {
        return this.hardcoreEnabled;
    }

    /**
     * Get whether the map features (e.g. strongholds) generation is enabled or disabled.
     */
    public boolean isMapFeaturesEnabled()
    {
        return this.mapFeaturesEnabled;
    }

    public WorldType getTerrainType()
    {
        return this.terrainType;
    }
}
