package ru.krista.fm.artemisreciver.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.krista.fm.artemisreciver.domain.MessageLog;

public interface MessageLogRepositories extends CrudRepository<MessageLog, Long> {
}
