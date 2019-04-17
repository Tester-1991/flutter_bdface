import 'package:simple_permissions/simple_permissions.dart';

///权限管理器
class PermissionManager {
  ///申请权限
  static Future<bool> requestPermission(List permissions) async {
    bool permissionResult = true;
    for (Permission permission in permissions) {
      bool result = await SimplePermissions.checkPermission(permission);

      if (!result) {
        var permissionStatus =
            await SimplePermissions.requestPermission(permission);
        if (permissionStatus != PermissionStatus.authorized) {
          permissionResult = false;
          break;
        }
      }
    }
    return permissionResult;
  }
}
