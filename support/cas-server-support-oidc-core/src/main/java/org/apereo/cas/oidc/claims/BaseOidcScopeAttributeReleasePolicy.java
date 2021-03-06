package org.apereo.cas.oidc.claims;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.services.AbstractRegisteredServiceAttributeReleasePolicy;
import org.apereo.cas.services.RegisteredService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is {@link BaseOidcScopeAttributeReleasePolicy}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
public abstract class BaseOidcScopeAttributeReleasePolicy extends AbstractRegisteredServiceAttributeReleasePolicy {
    private static final long serialVersionUID = -7302163334687300920L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseOidcScopeAttributeReleasePolicy.class);

    private List<String> allowedAttributes;

    @JsonIgnore
    private String scopeName;

    public BaseOidcScopeAttributeReleasePolicy(final String scopeName) {
        this.scopeName = scopeName;
    }

    public String getScopeName() {
        return scopeName;
    }

    public void setAllowedAttributes(final List<String> allowed) {
        this.allowedAttributes = allowed;
    }

    public List<String> getAllowedAttributes() {
        return this.allowedAttributes;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final BaseOidcScopeAttributeReleasePolicy rhs = (BaseOidcScopeAttributeReleasePolicy) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(getAllowedAttributes(), rhs.getAllowedAttributes())
                .append(getScopeName(), rhs.getScopeName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 133)
                .appendSuper(super.hashCode())
                .append(getAllowedAttributes())
                .append(getScopeName())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("allowedAttributes", getAllowedAttributes())
                .append("scopeName", scopeName)
                .toString();
    }

    @Override
    protected Map<String, Object> getAttributesInternal(final Principal principal,
                                                        final Map<String, Object> attributes, final RegisteredService service) {
        final Map<String, Object> resolvedAttributes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        resolvedAttributes.putAll(attributes);
        final Map<String, Object> attributesToRelease = new HashMap<>(resolvedAttributes.size());
        getAllowedAttributes().stream()
                .map(attr -> new Object[]{attr, resolvedAttributes.get(attr)}).filter(pair -> pair[1] != null)
                .forEach(attribute -> attributesToRelease.put((String) attribute[0], attribute[1]));
        return attributesToRelease;
    }
}
