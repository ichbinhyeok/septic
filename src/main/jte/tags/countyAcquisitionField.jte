@import com.example.septic.web.CountyAcquisitionFieldView
@param CountyAcquisitionFieldView field

<div class="county-acquisition-field">
    <label for="county-acquisition-${field.key()}">
        <span>${field.label()}@if (!field.required()) <small>optional</small>@endif</span>
        @if (field.select())
            <select id="county-acquisition-${field.key()}"
                    data-county-acquisition-field="${field.key()}"
                    required="${field.required()}">
                <option value="">Choose one</option>
                @for (var option : field.options())
                    <option value="${option}">${option}</option>
                @endfor
            </select>
        @else
            @if (field.multiline())
                <textarea id="county-acquisition-${field.key()}"
                          rows="4"
                          maxlength="2000"
                          autocomplete="${field.autocomplete()}"
                          placeholder="${field.placeholder()}"
                          data-county-acquisition-field="${field.key()}"
                          required="${field.required()}"></textarea>
            @else
                <input id="county-acquisition-${field.key()}"
                       type="${"email".equals(field.autocomplete()) ? "email" : "text"}"
                       maxlength="220"
                       autocomplete="${field.autocomplete()}"
                       placeholder="${field.placeholder()}"
                       data-county-acquisition-field="${field.key()}"
                       required="${field.required()}">
            @endif
        @endif
    </label>
    <button class="county-acquisition-field__copy"
            type="button"
            data-county-acquisition-field-copy
            aria-label="Copy ${field.label()}">Copy</button>
</div>
