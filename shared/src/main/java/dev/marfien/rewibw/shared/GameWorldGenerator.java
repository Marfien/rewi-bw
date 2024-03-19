package dev.marfien.rewibw.shared;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameWorldGenerator {

    EMPTY(new EmptyChunkGenerator());

    private final EmptyChunkGenerator generator;

}
