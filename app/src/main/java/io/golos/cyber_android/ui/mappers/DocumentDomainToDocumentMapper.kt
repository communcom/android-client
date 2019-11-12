package io.golos.cyber_android.ui.mappers

import com.squareup.moshi.Moshi
import io.golos.data.dto.DocumentEntity
import io.golos.domain.dto.PostDomain

class DocumentDomainToDocumentMapper : Function1<String?, PostDomain.DocumentDomain?> {

    override fun invoke(rawDocument: String?): PostDomain.DocumentDomain? {
        return rawDocument?.let {
            return Moshi.Builder().build()
                .adapter(DocumentEntity::class.java)
                .fromJson(it)
                ?.let { documentEntity ->
                PostDomain.DocumentDomain(
                    AttributesDomainToAttributesMapper().invoke(documentEntity.attributes),
                    ContentEntityListToContentDomainListMapper().invoke(documentEntity.content),
                    documentEntity.id,
                    documentEntity.type
                )
            }
        }
    }


}