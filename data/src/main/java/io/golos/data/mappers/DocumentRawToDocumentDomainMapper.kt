package io.golos.data.mappers

import com.squareup.moshi.Moshi
import io.golos.data.dto.DocumentEntity
import io.golos.domain.dto.PostDomain

class DocumentRawToDocumentDomainMapper : Function1<String?, PostDomain.DocumentDomain?> {

    override fun invoke(rawDocument: String?): PostDomain.DocumentDomain? {


        return rawDocument?.let {
            val moshi = Moshi.Builder().build()
            val jsonDocumentAdapter = moshi.adapter<DocumentEntity>(DocumentEntity::class.java)
            return jsonDocumentAdapter.fromJson(it)?.let { documentEntity ->
                PostDomain.DocumentDomain(
                    AttributesEntityToAttributesDomainMapper().invoke(documentEntity.attributes),
                    ContentEntityListToContentDomainListMapper().invoke(documentEntity.content),
                    documentEntity.id,
                    documentEntity.type
                )
            }
        }
    }


}