#import "FlutterBdfacePlugin.h"
#import <flutter_bdface/flutter_bdface-Swift.h>

@implementation FlutterBdfacePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterBdfacePlugin registerWithRegistrar:registrar];
}
@end
