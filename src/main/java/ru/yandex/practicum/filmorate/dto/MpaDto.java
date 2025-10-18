package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Mpa;

@Data
public class MpaDto {
    private Integer id;
    private String name;

    public static MpaDto fromMpa(Mpa mpa) {
        MpaDto dto = new MpaDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getName());
        return dto;
    }

    public Mpa toMpa() {
        Mpa mpa = new Mpa();
        mpa.setId(this.id);
        mpa.setName(this.name);
        return mpa;
    }
}
