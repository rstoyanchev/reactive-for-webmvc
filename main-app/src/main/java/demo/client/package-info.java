/**
 * <ul>
 * <li>[1a] RestTemplate sync, sequential calls
 * <li>[2a] WebClient deferred nature. Need to subscribe (consume, collect, block)
 * <li>[2b] Compose on completion of multiple requests
 * <li>[2c] Singe chain. Try also concatMap, .then().block(), .then(Publisher), etc.
 * <li>[2d] Retrieve vs exchange (consume body, ResponseEntity), builder options
 * <li>[2e] Inter-dependant, nested calls
 * <li>[3a] Streaming, lifecycle
 * </ul>
 */
package demo.client;