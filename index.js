import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  BackHandler,
  NativeModules,
  Platform,
  ActionSheetIOS,
  Dimensions
} from 'react-native';

import Overlay from './components/Overlay';
import Sheet from './components/Sheet';
import Button from './components/Button';

const { height, width } = Dimensions.get('window');
const styles = StyleSheet.create({
    actionSheetContainer: {
      ...StyleSheet.absoluteFillObject,
      height,
      width,
      position: 'absolute',
      zIndex: 10,
      backgroundColor: 'red'
      // backgroundColor: 'rgba(0, 0, 0, 0.5)',
    },
    buttonContainer: {
      overflow: 'hidden',
      backgroundColor: 'white',
      paddingBottom: 5,
      paddingTop: 5
    }
});

class RNShare {
  static open(options) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "ios") {
        ActionSheetIOS.showShareActionSheetWithOptions(options, (error) => {
          return reject({ error: error });
        }, (success, activityType) => {
          if(success) {
            return resolve({
              app: activityType
            });
          } else {
            reject({ error: "User did not share" });
          }
        });
      } else {
        NativeModules.RNShare.open(options,(e) => {
          return reject({ error: e });
        },(e) => {
          resolve({
            message: e
          });
        });
      }
    });
  }
  static shareSingle(options){
    if (Platform.OS === "ios" || Platform.OS === "android") {
      return new Promise((resolve, reject) => {
        NativeModules.RNShare.shareSingle(options,(e) => {
          return reject({ error: e });
        },(e) => {
          return resolve({
            message: e
          });
        });
      });
    } else {
      throw new Exception("not implemented");
    }
  }
}
class ShareSheet extends React.Component {
  componentDidMount() {
    BackHandler.addEventListener('hardwareBackPress',() => {
      if (this.props.visible) {
        this.props.onCancel();
        return true;
      }
      return false;
    });
  }
  render(){
    const { onCancel, visible, sheetHeight, children } = this.props;

    return (
      <Overlay visible={visible} {...this.props}>
        <View style={styles.actionSheetContainer}>
          <TouchableOpacity
              style={{flex:1}}
              onPress={onCancel}>
          </TouchableOpacity>
          <Sheet visible={visible} sheetHeight={sheetHeight}>
            <View style={styles.buttonContainer}>
              {children}
            </View>
          </Sheet>
        </View>
      </Overlay>
    )
  }
}


module.exports = RNShare;
module.exports.Overlay = Overlay;
module.exports.Sheet = Sheet;
module.exports.Button = Button;
module.exports.ShareSheet = ShareSheet;
