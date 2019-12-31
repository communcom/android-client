package io.golos.domain.repositories

import io.golos.domain.dto.AuthStateDomain
import io.golos.domain.requestmodel.AuthRequest

interface AuthStateRepository : Repository<AuthStateDomain, AuthRequest>