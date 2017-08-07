import React from 'react';
import {
  Animated,
  StyleSheet,
  View,
  TouchableHighlight
} from 'react-native';

const DEFAULT_ANIMATE_TIME = 100;
const styles = StyleSheet.create({
    fullOverlay: {
      ...StyleSheet.absoluteFillObject,
      position: 'absolute',
      zIndex: 1,
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      elevation: 2,
    },
    emptyOverlay: {
      width: 0,
      height: 0,
      backgroundColor: 'transparent',
      position: 'absolute'
    }
});
export default class Overlay extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      fadeAnim: new Animated.Value(0),
      overlayStyle: styles.emptyOverlay
    }
  }
  onAnimatedEnd() {
    if(!this.props.visible) {
        this.setState({overlayStyle:styles.emptyOverlay});
    }
  }
  componentWillReceiveProps(newProps) {
    if(newProps.visible){
      this.setState({overlayStyle: styles.fullOverlay});
    }
    return Animated.timing(this.state.fadeAnim, {
        toValue: newProps.visible ? 1 : 0,
        duration: DEFAULT_ANIMATE_TIME,
        useNativeDriver: true
    }).start(this.onAnimatedEnd.bind(this));
  }
  render() {
    return (
      <Animated.View style={[this.state.overlayStyle, {opacity: this.state.fadeAnim}]}>
          {this.props.children}
      </Animated.View>
    );
  }
}
