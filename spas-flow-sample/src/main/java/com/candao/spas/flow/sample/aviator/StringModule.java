package com.candao.spas.flow.sample.aviator;

import com.googlecode.aviator.annotation.Import;

@Import(ns = "str")
  public class StringModule {
    public static boolean isBlank(final String s) {
      return s == null || s.trim().length() == 0;
    }
  }