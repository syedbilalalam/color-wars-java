#ifndef F_COLOR_WARS_EXPORT_H
#define F_COLOR_WARS_EXPORT_H

/*
 * F_COLOR_WARS_BUILD_SHARED   - define when building the library as a shared lib (.dll/.so/.dylib)
 * F_COLOR_WARS_STATIC         - define when building/using the library as a static lib
 * F_COLOR_WARS_COMPILING      - define only in the library's own source files (not by consumers)
 */


/* Static builds need no import/export decoration */
#if defined(F_COLOR_WARS_STATIC)
	#define F_COLOR_WARS_API


/* Windows: __declspec(dllexport/dllimport) */
#elif defined(_WIN32) || defined(__CYGWIN__)
	#ifdef F_COLOR_WARS_COMPILING
		#ifdef __GNUC__
			#define F_COLOR_WARS_API __attribute__((dllexport))
		#else
			#define F_COLOR_WARS_API __declspec(dllexport)
		#endif // __GNUC__
	#else // F_COLOR_WARS_COMPILING
		#ifdef __GNUC__
			#define F_COLOR_WARS_API __attribute__((dllimport))
		#else
			#define F_COLOR_WARS_API __declspec(dllimport)
		#endif // __GNUC__
	#endif // F_COLOR_WARS_COMPILING


/* GCC/Clang on Linux, macOS, etc. */
#elif defined(__GNUC__) && __GNUC__ >= 4
	#define F_COLOR_WARS_API __attribute__((visibility("default")))
#else
	#define F_COLOR_WARS_API
#endif

/* Optional: local/hidden symbol marker for internal-only functions */
#if defined(_WIN32) || defined(__CYGWIN__)
	#define F_COLOR_WARS_LOCAL
#elif defined(__GNUC__) && __GNUC__ >= 4
	#define F_COLOR_WARS_LOCAL __attribute__((visibility("hidden")))
#else
	#define F_COLOR_WARS_LOCAL
#endif

#endif /* F_COLOR_WARS_EXPORT_H */