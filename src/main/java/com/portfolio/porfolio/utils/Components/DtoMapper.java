package com.portfolio.porfolio.utils.Components;

import java.util.stream.Collectors;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * @description Utility class for mapping between DTOs and entities using ModelMapper.
 * Provides methods to map individual objects and lists of objects.
 */
@Component
public class DtoMapper {
    // Model Mapper inyection
    private final ModelMapper modelMapper;

    public DtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * @description Maps a source object to an instance of the target class.
     * @param <S> The type of the source object.
     * @param <T> The type of the target object.
     * @param source The source object to be mapped.
     * @param targetClass The class of the target object.
     * @return An instance of the target class with mapped properties from the source object.
     */
    public <S, T> T map(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, targetClass);
    }

    /**
     * @description Maps a list of source objects to a list of instances of the target class.
     * @param <S> The type of the source objects.
     * @param <T> The type of the target objects.
     * @param sourceList The list of source objects to be mapped.
     * @param targetClass The class of the target objects.
     * @return A list of instances of the target class with mapped properties from the source objects.
     */
    public <S, T> List<T> mapList(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null) {
            return null;
        }
        return sourceList.stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }
}
