package de.presti.ree6.module.actions.customevents.container;

import de.presti.ree6.module.actions.ActionInfo;
import de.presti.ree6.module.actions.customevents.IEventAction;
import de.presti.ree6.sql.SQLSession;
import de.presti.ree6.sql.entities.custom.CustomEventAction;
import de.presti.ree6.sql.entities.custom.CustomEventTyp;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Utility class used to create CustomEventContainers.
 */
public class CustomEventContainerCreator {

    /**
     * A Cache for all StreamActions.
     */
    private static final HashMap<String, Class<? extends IEventAction>> cachedActions = new HashMap<>();

    /**
     * Constructor should not be called, since it is a utility class that doesn't need an instance.
     */
    private CustomEventContainerCreator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Load all actions.
     */
    public static void loadAll() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.addClassLoaders(ClasspathHelper.staticClassLoader());
        Reflections reflections = new Reflections(configurationBuilder);
        Set<Class<? extends IEventAction>> classes = reflections.getSubTypesOf(IEventAction.class);

        for (Class<? extends IEventAction> aClass : classes) {
            if (aClass.isAnnotationPresent(ActionInfo.class)) {
                String actionName = aClass.getAnnotation(ActionInfo.class).name();
                cachedActions.computeIfAbsent(actionName, s -> aClass);
            }
        }
    }

    /**
     * Get the Class of a StreamAction.
     *
     * @param action The Name of the Action.
     * @return The Class of the Action.
     */
    public static Class<? extends IEventAction> getAction(String action) {
        action = action.trim().toLowerCase();

        if (cachedActions.containsKey(action)) return cachedActions.get(action);

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.addClassLoaders(ClasspathHelper.staticClassLoader());
        Reflections reflections = new Reflections(configurationBuilder);
        Set<Class<? extends IEventAction>> classes = reflections.getSubTypesOf(IEventAction.class);

        for (Class<? extends IEventAction> aClass : classes) {
            if (aClass.isAnnotationPresent(ActionInfo.class) && aClass.getAnnotation(ActionInfo.class).name().trim().equalsIgnoreCase(action)) {
                cachedActions.computeIfAbsent(action, s -> aClass);

                return aClass;
            }
        }

        return null;
    }

    /**
     * Get all CustomEventContainer.
     *
     * @param typ The typ of the CustomEventAction.
     * @return A List of CustomEventContainer.
     */
    public static Mono<List<CustomEventContainer>> getContainers(CustomEventTyp typ) {
        return SQLSession.getSqlConnector().getSqlWorker()
                .getEntityList(new CustomEventAction(), "FROM CustomEventAction WHERE event = :typ", Map.of("typ", typ.name()))
                .map(list -> list.stream().map(CustomEventContainer::new).toList());
    }

    /**
     * Get all CustomEventContainer.
     *
     * @param guildId The related Guild.
     * @param typ     The typ of the CustomEventAction.
     * @return A List of CustomEventContainer.
     */
    public static Mono<List<CustomEventContainer>> getContainers(String guildId, CustomEventTyp typ) {
        return SQLSession.getSqlConnector().getSqlWorker()
                .getEntityList(new CustomEventAction(), "FROM CustomEventAction WHERE guildId = :guild AND event = :typ", Map.of("guild", guildId, "typ", typ.name()))
                .map(list -> list.stream().map(CustomEventContainer::new).toList());
    }

}
