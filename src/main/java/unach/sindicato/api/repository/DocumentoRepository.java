package unach.sindicato.api.repository;

import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;
import unach.sindicato.api.persistence.documentos.Documento;

import java.util.Optional;

@Repository
public interface DocumentoRepository extends UddRepository<Documento> {
    @Aggregation(pipeline = {
            "{$match: {_id: ?0}}",
            "{$project: {bytes: 0}}"
    })
    Optional<Documento> findByIdExcludingBytes(@NonNull ObjectId id);
}