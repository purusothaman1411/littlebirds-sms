package com.littlebirds.sms.dto;

import jakarta.validation.groups.Default;

/**
 * Validation group for rules that apply only when creating (for example the ID in the body, which is
 * ignored on update because the ID comes from the URL). It extends Default, so every normal rule also runs.
 */
public interface OnCreate extends Default {
}
