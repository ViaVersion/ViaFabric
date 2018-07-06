package com.github.creeper123123321.viarift.platform;

import us.myles.ViaVersion.api.ViaVersionConfig;

import java.util.ArrayList;
import java.util.List;

public class VRViaConfig implements ViaVersionConfig {
    @Override
    public boolean isCheckForUpdates() {
        return false;
    }

    @Override
    public boolean isPreventCollision() {
        return true;
    }

    @Override
    public boolean isNewEffectIndicator() {
        return true;
    }

    @Override
    public boolean isShowNewDeathMessages() {
        return false;
    }

    @Override
    public boolean isSuppressMetadataErrors() {
        return false;
    }

    @Override
    public boolean isShieldBlocking() {
        return true;
    }

    @Override
    public boolean isHologramPatch() {
        return true;
    }

    @Override
    public boolean isPistonAnimationPatch() {
        return false;
    }

    @Override
    public boolean isBossbarPatch() {
        return true;
    }

    @Override
    public boolean isBossbarAntiflicker() {
        return false;
    }

    @Override
    public boolean isUnknownEntitiesSuppressed() {
        return false;
    }

    @Override
    public double getHologramYOffset() {
        return -0.96;
    }

    @Override
    public boolean isAutoTeam() {
        return true;
    }

    @Override
    public boolean isBlockBreakPatch() {
        return false;
    }

    @Override
    public int getMaxPPS() {
        return -1;
    }

    @Override
    public String getMaxPPSKickMessage() {
        return null;
    }

    @Override
    public int getTrackingPeriod() {
        return -1;
    }

    @Override
    public int getWarningPPS() {
        return -1;
    }

    @Override
    public int getMaxWarnings() {
        return -1;
    }

    @Override
    public String getMaxWarningsKickMessage() {
        return "";
    }

    @Override
    public boolean isAntiXRay() {
        return false;
    }

    @Override
    public boolean isSendSupportedVersions() {
        return false;
    }

    @Override
    public boolean isStimulatePlayerTick() {
        return true;
    }

    @Override
    public boolean isItemCache() {
        return false;
    }

    @Override
    public boolean isNMSPlayerTicking() {
        return false;
    }

    @Override
    public boolean isReplacePistons() {
        return false;
    }

    @Override
    public int getPistonReplacementId() {
        return -1;
    }

    @Override
    public boolean isForceJsonTransform() {
        return false;
    }

    @Override
    public boolean is1_12NBTArrayFix() {
        return true;
    }

    @Override
    public boolean is1_13TeamColourFix() {
        return false;
    }

    @Override
    public boolean is1_12QuickMoveActionFix() {
        return false;
    }

    @Override
    public List<Integer> getBlockedProtocols() {
        return new ArrayList<>();
    }

    @Override
    public String getBlockedDisconnectMsg() {
        return "";
    }

    @Override
    public String getReloadDisconnectMsg() {
        return "";
    }
}
