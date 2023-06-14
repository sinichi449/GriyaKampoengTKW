package net.bagusekasaputra.griyakampoengtkw.di

import javax.inject.Qualifier

@Qualifier
annotation class RootReference

@Qualifier
annotation class TahapanReference

@Qualifier
annotation class InternalDir

@Qualifier
annotation class ExternalDir

/**
 * Refres to any deprecated Repositories.
 */
@Qualifier
annotation class Legacy

/**
 * Refers to any maintained Repositories.
 */
@Qualifier
annotation class Default