package top.vulpine.simpleLobby.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;

@SuppressWarnings("deprecation")
@Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
public abstract class BaseConfig extends OkaeriConfig {}