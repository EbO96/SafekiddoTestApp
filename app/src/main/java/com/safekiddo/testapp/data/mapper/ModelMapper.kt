package com.safekiddo.testapp.data.mapper

/**
 * Base class which implementation maps one model to another.
 * In most cases it maps API response model to entity model.
 */
abstract class ModelMapper<I, O> {

    abstract fun map(input: I): O
}