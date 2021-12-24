package de.presti.ree6.logger;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.util.EnumSet;

public class LoggerRoleData {

    // The ID of the Role.
    private long roleId;

    // If it is a Name change Event, two variables to store the previous and current Name.
    private String previousName, currentName;

    // If it is a Color change Event, two variables to store the previous and current Color.
    private Color previousColor, currentColor;

    // If it is a Permission change Event, two variables to store the previous and current Permissions.
    private EnumSet<Permission> previousPermission, currentPermission;

    // Other information about the Role,
    private boolean IsCreated, IsDeleted, IsHoisted, IsMentioned, changedMentioned, changedHoisted;

    /**
     * A Constructor for a {@link Role} based Event.
     *
     * @param role the {@link Role} of the Event.
     */
    public LoggerRoleData(Role role) {
        this.roleId = role.getIdLong();
        this.currentName = role.getName();
        this.currentColor = role.getColor();
        this.currentPermission = role.getPermissions();
        setChangedHoisted(role.isHoisted());
        setChangedMentioned(role.isMentionable());
    }

    /**
     * A Constructor for a Name change Event.
     *
     * @param roleId       the ID of the {@link Role}.
     * @param previousName the previous Name of the {@link Role}.
     * @param currentName  the current Name of the {@link Role}.
     */
    public LoggerRoleData(long roleId, String previousName, String currentName) {
        this.roleId = roleId;
        this.previousName = previousName;
        this.currentName = currentName;
    }

    /**
     * A Constructor for a Name change Event.
     *
     * @param roleId       the ID of the {@link Role}.
     * @param previousName the previous Name of the {@link Role}.
     * @param currentName  the current Name of the {@link Role}.
     * @param isCreated    the creation State of the {@link Role}.
     */
    public LoggerRoleData(long roleId, String previousName, String currentName, boolean isCreated) {
        this.roleId = roleId;
        this.previousName = previousName;
        this.currentName = currentName;
        IsCreated = isCreated;
    }

    /**
     * A Constructor for a creation Event.
     *
     * @param roleId      the ID of the {@link Role}.
     * @param currentName the current Name of the {@link Role}.
     * @param isCreated   the creation State of the {@link Role}.
     */
    public LoggerRoleData(long roleId, String currentName, boolean isCreated) {
        this.roleId = roleId;
        this.currentName = currentName;
        IsCreated = isCreated;
        IsDeleted = !isCreated;
    }

    /**
     * A Constructor for a Name change Event.
     *
     * @param roleId        the ID of the {@link Role}.
     * @param previousColor the previous Color of the {@link Role}.
     * @param currentColor  the current Color of the {@link Role}.
     */
    public LoggerRoleData(long roleId, Color previousColor, Color currentColor) {
        this.roleId = roleId;
        this.previousColor = previousColor;
        this.currentColor = currentColor;
    }

    /**
     * A Constructor for a Color change Event.
     *
     * @param roleId        the ID of the {@link Role}.
     * @param previousColor the previous Color of the {@link Role}.
     * @param currentColor  the current Color of the {@link Role}.
     * @param isCreated     the creation State of the {@link Role}.
     */
    public LoggerRoleData(long roleId, Color previousColor, Color currentColor, boolean isCreated) {
        this.roleId = roleId;
        this.previousColor = previousColor;
        this.currentColor = currentColor;
        IsCreated = isCreated;
    }

    /**
     * A Constructor for a Permission change Event.
     *
     * @param roleId             the ID of the {@link Role}.
     * @param previousPermission the previous Permissions of the {@link EnumSet<Permission>}.
     * @param currentPermission  the current Permissions of the {@link EnumSet<Permission>}.
     */
    public LoggerRoleData(long roleId, EnumSet<Permission> previousPermission, EnumSet<Permission> currentPermission) {
        this.roleId = roleId;
        this.previousPermission = previousPermission;
        this.currentPermission = currentPermission;
    }

    /**
     * A Constructor for a Permission change Event.
     *
     * @param roleId             the ID of the {@link Role}.
     * @param previousPermission the previous Permissions of the {@link EnumSet<Permission>}.
     * @param currentPermission  the current Permissions of the {@link EnumSet<Permission>}.
     * @param isCreated          the creation State of the {@link Role}.
     */
    public LoggerRoleData(long roleId, EnumSet<Permission> previousPermission, EnumSet<Permission> currentPermission, boolean isCreated) {
        this.roleId = roleId;
        this.previousPermission = previousPermission;
        this.currentPermission = currentPermission;
        IsCreated = isCreated;
    }

    /**
     * A Constructor for a {@link Role} base Event.
     *
     * @param roleId      the ID of the {@link Role}.
     * @param currentName the current Name of the {@link Role}.
     * @param isCreated   the creation State of the {@link Role}.
     * @param isDeleted   the deletion State of the {@link Role}.
     * @param isHoisted   the hoisted State of the {@link Role}.
     * @param isMentioned the mentioned State of the {@link Role}.
     */
    public LoggerRoleData(long roleId, String currentName, boolean isCreated, boolean isDeleted, boolean isHoisted, boolean isMentioned) {
        this.roleId = roleId;
        this.currentName = currentName;
        IsCreated = isCreated;
        IsDeleted = isDeleted;
        setChangedHoisted(isHoisted);
        setChangedMentioned(isMentioned);
    }

    /**
     * Get ID of the {@link Role}.
     * @return the ID of the {@link Role} as {@link Long}.
     */
    public long getRoleId() {
        return roleId;
    }

    /**
     * Change the {@link Role} ID that is associated with the Event.
     * @param roleId the ID of the {@link Role}
     */
    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    /**
     * Get the Previous Name of the {@link Role}.
     *
     * @return the previous Name as {@link String}.
     */
    public String getPreviousName() {
        return previousName;
    }

    /**
     * Change the previous Name of the {@link Role}.
     *
     * @param previousName the new previous Name as {@link String}.
     */
    public void setPreviousName(String previousName) {
        this.previousName = previousName;
    }

    /**
     * Get the current Name of the {@link Role}.
     *
     * @return the current Name as {@link String}
     */
    public String getCurrentName() {
        return currentName;
    }

    /**
     * Change the current Name of the {@link Role}.
     *
     * @param currentName the new current Name as {@link String}.
     */
    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }

    /**
     * Get the previous Color of the {@link Role}.
     *
     * @return the previous Color as {@link Color}
     */
    public Color getPreviousColor() {
        return previousColor;
    }

    /**
     * Change the previous Color of the {@link Role}.
     *
     * @param previousColor the new previous Color as {@link Color}.
     */
    public void setPreviousColor(Color previousColor) {
        this.previousColor = previousColor;
    }

    /**
     * Get the current Color of the {@link Role}.
     *
     * @return the current Color as {@link Color}
     */
    public Color getCurrentColor() {
        return currentColor;
    }

    /**
     * Change the current Color of the {@link Role}.
     *
     * @param currentColor the new current Color as {@link Color}.
     */
    public void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }

    /**
     * Get the previous {@link EnumSet<Permission>} of the {@link Role}.
     *
     * @return the previous {@link EnumSet<Permission>} of the {@link Role}.
     */
    public EnumSet<Permission> getPreviousPermission() {
        return previousPermission;
    }

    /**
     * Change the previous {@link EnumSet<Permission>} of the {@link Role}.
     *
     * @param previousPermission the new previous {@link EnumSet<Permission>} of the {@link Role}.
     */
    public void setPreviousPermission(EnumSet<Permission> previousPermission) {
        this.previousPermission = previousPermission;
    }

    /**
     * Get the current {@link EnumSet<Permission>} of the {@link Role}.
     *
     * @return the current {@link EnumSet<Permission>} of the {@link Role}.
     */
    public EnumSet<Permission> getCurrentPermission() {
        return currentPermission;
    }

    /**
     * Change the current {@link EnumSet<Permission>} of the {@link Role}.
     *
     * @param currentPermission the new current {@link EnumSet<Permission>} of the {@link Role}.
     */
    public void setCurrentPermission(EnumSet<Permission> currentPermission) {
        this.currentPermission = currentPermission;
    }

    /**
     * Get the current creation State of the {@link Role}.
     * @return the current State as {@link Boolean}.
     */
    public boolean isCreated() {
        return IsCreated;
    }

    /**
     * Change the current creation state of the {@link Role}.
     * @param created the new state of creation.
     */
    public void setCreated(boolean created) {
        IsCreated = created;
    }

    /**
     * Get the current deletion State of the Role.
     * @return the current State as {@link Boolean}.
     */
    public boolean isDeleted() {
        return IsDeleted;
    }

    /**
     * Change the current deleted state of the {@link Role}.
     * @param deleted the new state of deletion.
     */
    public void setDeleted(boolean deleted) {
        IsDeleted = deleted;
    }

    /**
     * Get the current hoisted State of the Role.
     * @return the current State as {@link Boolean}.
     */
    public boolean isHoisted() {
        return IsHoisted;
    }

    /**
     * Change the current hoisted state of the {@link Role}.
     * @param hoisted the new state of hoisted.
     */
    public void setHoisted(boolean hoisted) {
        IsHoisted = hoisted;
        changedHoisted = true;
    }

    /**
     * Get the current mentioned State of the Role.
     * @return the current State as {@link Boolean}.
     */
    public boolean isMentioned() {
        return IsMentioned;
    }

    /**
     * Change the current mentioned state of the {@link Role}.
     * @param mentioned the new state of mentioned.
     */
    public void setMentioned(boolean mentioned) {
        IsMentioned = mentioned;
        changedMentioned = true;
    }

    /**
     * Get the current mentioned changed State of the Role.
     * @return the current State as {@link Boolean}.
     */
    public boolean isChangedMentioned() {
        return changedMentioned;
    }

    /**
     * Change the current mentioned change state of the {@link Role}.
     * @param changedMentioned the new state of mentioned change.
     */
    public void setChangedMentioned(boolean changedMentioned) {
        this.changedMentioned = changedMentioned;
    }

    /**
     * Get the current hoisted changed State of the Role.
     * @return the current State as {@link Boolean}.
     */
    public boolean isChangedHoisted() {
        return changedHoisted;
    }

    /**
     * Change the current hoisted change state of the {@link Role}.
     * @param changedHoisted the new state of hoisted change.
     */
    public void setChangedHoisted(boolean changedHoisted) {
        this.changedHoisted = changedHoisted;
    }
}
